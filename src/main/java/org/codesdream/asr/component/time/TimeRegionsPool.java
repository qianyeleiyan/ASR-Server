package org.codesdream.asr.component.time;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.Opt;
import org.codesdream.asr.model.time.TimeBlock;

import java.io.Serializable;
import java.util.*;

@Slf4j
@Data
public class TimeRegionsPool implements Serializable {

    private Map<Integer, TimeRegion> freeRegions = new HashMap<>();

    private Map<Integer, TimeRegion> assignedRegions = new HashMap<>();

    private int maxCode;

    private transient TimeBlocksPool timeBlocksPool;

    private transient TimeBlockCodeGenerator codeGenerator;

    private transient TimeScaleStatus scaleStatus;

    private transient RegionToolBox regionToolBox;


    public TimeRegionsPool(TimeBlocksPool timeBlocksPool){
        this.timeBlocksPool = timeBlocksPool;
    }

    /**
     * 执行空闲时间段的分配(跨越时间段无效)
     * @param startCode 起始时间块编号
     * @param endCode 截止时间块编号
     * @param apmId 分配编号
     */
    public void assignTimeRegion(Integer startCode, Integer endCode, String apmId){

        assert startCode <= endCode && apmId != null;

        // 遍历时间段
        for(Integer regionCode : freeRegions.keySet()){
            if(regionCode <= startCode){
                TimeRegion region = freeRegions.get(regionCode);
                assert  region != null;
                // 筛选满足条件的时间段
                if(region.maxCode >= endCode){

                    // 执行三段剪切
                    List<Optional<TimeRegion>> regionsList =
                            regionToolBox.cutTimeRegionAutomatic(region, startCode, endCode);

                    // 移除旧的时间段
                    freeRegions.remove(regionCode);

                    // 处理前段
                    Optional<TimeRegion> regionOptional = regionsList.get(0);
                    regionOptional.ifPresent(timeRegion -> freeRegions.put(timeRegion.getMinCode(), timeRegion));

                    // 处理后段
                    regionOptional = regionsList.get(1);
                    regionOptional.ifPresent(timeRegion -> freeRegions.put(timeRegion.getMinCode(), timeRegion));


                    // 整理时间段
                    mergeFree();

                    // 处理中段
                    Optional<TimeRegion> targetRegion = regionsList.get(2);
                    if (targetRegion.isPresent()){
                        for(TimeBlockInstance instance : targetRegion.get().getTimeBlockInstanceMap().values()){
                            timeBlocksPool.assignedTimeBlock(instance, apmId);
                        }
                        // 加入已分配时间段
                        assignedRegions.put(targetRegion.get().getMinCode(), targetRegion.get());
                        // 整理已分配时间段
                        mergeAssigned();
                    }

                    break;
                }
            }
        }
    }

    /**
     * 擦除时间段的分配状态
     * @param startCode 开始时间块编号
     * @param endCode 结束时间块编号
     */
    public void removeAssignedTimeRegion(int startCode, int endCode){

        assert startCode <= endCode;

        // 遍历时间段
        for(Integer regionCode : assignedRegions.keySet()){
            if(regionCode <= startCode){
                TimeRegion region = assignedRegions.get(regionCode);

                // 筛选满足条件的时间段
                if(region.maxCode >= endCode){

                    // 执行三段剪切
                    List<Optional<TimeRegion>> regionsList =
                            regionToolBox.cutTimeRegionAutomatic(region, startCode, endCode);

                    // 移除旧的时间段
                    assignedRegions.remove(regionCode);

                    // 处理前段
                    Optional<TimeRegion> regionOptional = regionsList.get(0);
                    regionOptional.ifPresent(timeRegion -> assignedRegions.put(timeRegion.getMinCode(), timeRegion));

                    // 处理后段
                    regionOptional = regionsList.get(1);
                    regionOptional.ifPresent(timeRegion -> assignedRegions.put(timeRegion.getMinCode(), timeRegion));


                    // 整理时间段
                    mergeAssigned();

                    // 中段提取
                    Optional<TimeRegion> targetRegion = regionToolBox.cutTimeRegion(region, startCode, endCode);
                    if (targetRegion.isPresent()){
                        for(TimeBlockInstance instance : targetRegion.get().getTimeBlockInstanceMap().values()){
                            // 释放时间块
                            timeBlocksPool.freeTimeBlock(instance);
                        }

                        // 加入已分配时间段
                        freeRegions.put(targetRegion.get().getMinCode(), targetRegion.get());
                        // 整理已分配时间段
                        mergeFree();
                    }

                    break;
                }
            }
        }
    }

    // 清空时间段池
    public void clear(){
        this.freeRegions.clear();;
        this.assignedRegions.clear();
    }

    public void bufferedInit(TimeBlockCodeGenerator codeGenerator){
        this.codeGenerator = codeGenerator;
        for(TimeRegion region : freeRegions.values()){
            region.setCodeGenerator(codeGenerator);
        }
        for(TimeRegion region : assignedRegions.values()){
            region.setCodeGenerator(codeGenerator);
        }
    }

    public void mergeFree(){
        mergeRegions(freeRegions);
    }

    public void mergeAssigned(){
        mergeRegions(assignedRegions);
    }


    // 整理时间段表（修复剪切痕迹）
    private void mergeRegions(Map<Integer, TimeRegion> regionMap){

        List<TimeRegion> regionSet = new ArrayList<>(regionMap.values());

        Collections.sort(regionSet);

        Iterator<TimeRegion> iterator = regionSet.iterator();

        TimeRegion lastRegion = null;
        while (iterator.hasNext()){
            TimeRegion region = iterator.next();

            if(lastRegion != null && region.isNeighbour(lastRegion)){
                regionMap.remove(region.minCode);
                regionMap.remove(lastRegion.minCode);
                region.merge(lastRegion);

                regionMap.put(region.minCode, region);

            }

            lastRegion = region;
        }
    }


    /**
     * 执行时间段池初始化，完成时间块的分配
     */
    public void mergeTimeBlocks(){
        int lastStatus = TimeBlockStatus.FREE;
        TimeRegion lastRegion= null;

        Calendar calendar = codeGenerator.getInstanceCalendar();

        log.info(String.format("Check Current Code: %d", codeGenerator.getCurrent()));

        for(int i = codeGenerator.getCurrent(); i <= maxCode; i = codeGenerator.getNext(calendar)){

            TimeBlockInstance instance = timeBlocksPool.getTimeBlockInstance(i, calendar.get(Calendar.DAY_OF_WEEK));

            // 检测被禁用的时间块
            if(instance == null){
                lastStatus = TimeBlockStatus.DISABLED;
                continue;
            }

            // 得到时间块的状态
            int status = instance.getStatus();

            if(status == TimeBlockStatus.FREE){
                TimeRegion region;

                if(lastStatus == status){
                    // 第一个时间段初始化
                    if(lastRegion == null){
                        region = new TimeRegion(codeGenerator);
                        lastRegion = region;
                        freeRegions.put(instance.getCode(), region);
                    }
                    else region = lastRegion;
                }
                else{
                    // 新增时间段
                    region = new TimeRegion(codeGenerator);
                    lastRegion = region;
                    freeRegions.put(instance.getCode(), region);
                }

                region.simpleMergeBack(instance);

                lastStatus = TimeBlockStatus.FREE;
            }

            //下一个时间块已被分配
            if(status == TimeBlockStatus.ASSIGNED){
                TimeRegion region;

                if(lastStatus == status) region = lastRegion;
                else{
                    // 新增时间段
                    region = new TimeRegion(codeGenerator);
                    lastRegion = region;
                    assignedRegions.put(instance.getCode(), region);
                }

                region.merge(instance);

                // 记录时间块状态
                lastStatus = TimeBlockStatus.ASSIGNED;
            }

        }
    }

    /**
     * 刷新时间段池，剔除过期时间块
     * @param current 当前时间的块编号
     */
    public void update(int current){
        List<TimeRegion> timeRegions = new ArrayList<>();

        timeRegions.addAll(assignedRegions.values());
        timeRegions.addAll(freeRegions.values());

        for(TimeRegion region : timeRegions){
            int minCode = region.minCode;
            int maxCode = region.maxCode;

            // 被删除的最大的时间编块号
            int maxDeleteCode = region.update(current);

            Calendar calendar = codeGenerator.getCalendarFromCode(region.minCode);
            for(int i = minCode; i <= maxDeleteCode; i = codeGenerator.getNext(calendar)){
                timeBlocksPool.expiredTimeBlock(i);
            }

            if(maxDeleteCode == maxCode){
                if(freeRegions.get(minCode) != null){
                    freeRegions.remove(minCode);
                }
                else{
                    assignedRegions.remove(minCode);
                }
            }
        }
    }

}
