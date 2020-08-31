package org.codesdream.asr.component.time;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.Opt;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class RegionToolBox {

    @Resource
    private TimeBlockCodeGenerator codeGenerator;

    /**
     * 根据给出的时间编号段获得在这个时间编号段之间的时间块
     * @param region 时间段
     * @param startCode 时间编号开始
     * @param endCode 时间编号结束
     * @return 时间段列表
     */
    public List<TimeBlockInstance> getTimeBlockFroRange(TimeRegion region,
                                                         int startCode,
                                                         int endCode){

        List<TimeBlockInstance> instanceList = new ArrayList<>();

        // 执行断言
        assert startCode >= region.minCode && endCode <= region.maxCode;

        // 运算加速
        Calendar calendar = codeGenerator.getCalendarFromCode(startCode);
        for(int i = startCode; i <= endCode; i = codeGenerator.getNext(calendar)){
            TimeBlockInstance blockInstance = region.getTimeBlockInstanceMap().get(i);
            if(blockInstance == null){
                log.error(String.format("TimeBlockInstance Null Occurred For Code %s", i));
                if(i <= codeGenerator.getCurrent()) {
                    log.error(String.format("TimeBlockInstance Expired For Code %s", i));
                    continue;
                }
            }
            instanceList.add(blockInstance);
        }
        return instanceList;
    }


    /**
     *  获得收到时间区间影响的时间块表
     * @param timeRegion 时间段
     * @param apmPlan 时间分配方案
     * @return 时间块列表
     */
    public List<TimeBlockInstance> getInfluencedTimeBlocks(TimeRegion timeRegion, TimeAPMPlan apmPlan){
        // 检查边界条件
        if(apmPlan.startCode >= timeRegion.minCode && apmPlan.endCode <= timeRegion.maxCode)
            return getTimeBlockFroRange(timeRegion, apmPlan.startCode, apmPlan.endCode);
        else return new ArrayList<>();
    }


    /**
     * 根据最短时间段长度进行方案的预检查与预筛选
     * @param regionMap 时间段表
     * @param duration 时间区间大小
     * @return 时间段表
     */
    public Map<Integer, TimeRegion> getAvailableRegions(Map<Integer, TimeRegion> regionMap, Integer duration){
        Map<Integer, TimeRegion> availableList = new HashMap<>();

        // 遍历时间段
        for(Map.Entry<Integer, TimeRegion> regionEntry : regionMap.entrySet()){
            // 筛选满足题意的时间段
            if(regionEntry.getValue().getDuration() >= duration){
                TimeRegion region = regionEntry.getValue();

                log.info(String.format("Available TimeRegion From %d To %d Containing TimeBlocks %d",
                        region.minCode, region.maxCode, region.timeBlockInstanceMap.size()));

                availableList.put(regionEntry.getKey(), regionEntry.getValue());
            }
        }
        return availableList;
    }

    /**
     * 生成合适的时间段计算作用域
     * @param endCode 任务截止时间
     * @param freeRegions 总空闲时间
     * @return 时间段表
     */
    public HashMap<Integer, TimeRegion> getSuitableTimeRegionForCalculation(Integer endCode,
                                                                            Map<Integer, TimeRegion> freeRegions){
        try {
            HashMap<Integer, TimeRegion> tempFreeRegions = new HashMap<>();

            for (TimeRegion timeRegion : freeRegions.values()) {

                // 如果子时间段在截止时间前
                if (timeRegion.maxCode <= endCode)
                    tempFreeRegions.put(timeRegion.getMinCode(), (TimeRegion) timeRegion.clone());
                else {

                    // 如果时间段跨越截止时间
                    if (timeRegion.minCode < endCode) {
                        // 对被截止时间段分割时间段进行剪切备份
                        TimeRegion region = new TimeRegion(codeGenerator);

                        // 执行遍历
                        Calendar calendar = codeGenerator.getCalendarFromCode(timeRegion.minCode);
                        for (int i = timeRegion.minCode; i <= endCode; i = codeGenerator.getNext(calendar)) {
                            region.simpleMergeBack(timeRegion.timeBlockInstanceMap.get(i));
                        }

                        log.info(String.format("Suitable TimeRegion From %d To %d Containing TimeBlocks %d",
                                region.minCode, region.maxCode, region.timeBlockInstanceMap.size()));

                        // 加入新的时间段
                        tempFreeRegions.put(region.getMinCode(), region);
                    }
                    // 如果时间段恰好为截止时间
                    else if(timeRegion.minCode.equals(endCode)){
                        tempFreeRegions.put(timeRegion.minCode, (TimeRegion) timeRegion.clone());
                    }
                }
            }

            return tempFreeRegions;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new InnerDataTransmissionException(e.getMessage());
        }
    }

    // 执行时间段剪切
    public Optional<TimeRegion> cutTimeRegion(TimeRegion region, int startCode, int endCode){
        TimeRegion newRegion = new TimeRegion(codeGenerator);
        newRegion.minCode = startCode;
        newRegion.maxCode = endCode;

        log.info(String.format("Cutting TimeRegion From %d To %d Containing TimeBlocks %d",
                region.minCode, region.maxCode, region.timeBlockInstanceMap.size()));

        log.info(String.format("Cutting Range From %d To %d", startCode, endCode));
        // 时间块抽取
        for(TimeBlockInstance timeBlockInstance
                : this.getTimeBlockFroRange(region, newRegion.minCode, newRegion.maxCode)){
            newRegion.timeBlockInstanceMap.put(timeBlockInstance.getCode(), timeBlockInstance);
        }
        if(newRegion.size() > 0)
            return Optional.of(newRegion);

        return Optional.empty();
    }

    /**
     * 对时间段执行三段剪切
     * @return 剪切结果(前、后、中)
     */
    public List<Optional<TimeRegion>> cutTimeRegionAutomatic(TimeRegion region, int startCode, int endCode){
        List<Optional<TimeRegion>> resultList = new ArrayList<>();

        log.info("Front Cutting..");
        // 前段剪切
        if(region.minCode < startCode){
            Optional<TimeRegion> timeRegion =
                    this.cutTimeRegion(region, region.minCode, codeGenerator.getBefore(startCode));
            resultList.add(timeRegion);
        }
        else resultList.add(Optional.empty());

        log.info("Front Cutting Done.");

        log.info("Behind Cutting..");
        // 后段剪切
        if(region.maxCode > endCode){
            Optional<TimeRegion> timeRegion = this.cutTimeRegion(region, codeGenerator.getNext(endCode), region.maxCode);
            resultList.add(timeRegion);
        }
        else resultList.add(Optional.empty());

        log.info("Behind Cutting Done.");

        log.info("Middle Cutting..");
        // 中段剪切
        Optional<TimeRegion> targetRegion = this.cutTimeRegion(region, startCode, endCode);
        if (targetRegion.isPresent()){
            resultList.add(targetRegion);
        }
        else resultList.add(Optional.empty());

        log.info("Middle Cutting Done.");

        return resultList;
    }

    public void planTAMListMaker(TimeAppointTask appointTask){

        int frequency = appointTask.duration / appointTask.singleMax;

        List<Integer> durations = new ArrayList<>();

        for(int i = 0; i < frequency; i++){
            durations.add(appointTask.singleMax);
        }

        appointTask.addTimeApartMethod(durations);
    }

    public void durationApart(TimeAppointTask appointTask){

        // 缓存重要参数
        int totalDuration = appointTask.duration;
        int minDuration = appointTask.singleMin;
        int maxDuration = appointTask.singleMax;

        List<List<Integer>> timeApartMethodList = new ArrayList<>();

        // 执行递归搜索
        doApartTimeDuration(new ArrayList<>(), timeApartMethodList, totalDuration, minDuration, maxDuration);

        for(List<Integer> timeApartMethod : timeApartMethodList){
            appointTask.addTimeApartMethod(timeApartMethod);
        }
    }

    /**
     * 递归搜索拆分方案
     * @param durations 已缓存时间区间列表
     * @param timeApartMethodList 拆分方案列表
     * @param lastDuration 剩余时间区间
     * @param minDuration 最短时间区间
     * @param maxDuration 最长时间区间
     */
    private void doApartTimeDuration(List<Integer> durations,
                                     List<List<Integer>> timeApartMethodList,
                                     int lastDuration,
                                     int minDuration,
                                     int maxDuration){

        for(int i = minDuration; i <= maxDuration; i++){

            // 检测是否满足必要条件
            if(lastDuration - minDuration < 0) return;

            // 新建时间分配方案
            List<Integer> nextDurations = new ArrayList<>(durations);
            nextDurations.add(i);
            if (lastDuration - i > 0) {
                //继续递归搜索
                doApartTimeDuration(nextDurations,
                        timeApartMethodList,
                        lastDuration - i,
                        minDuration,
                        maxDuration);
            }
            else if(lastDuration - i < 0){
                return;
            }
            else{
                // 添加满足条件的拆分方案
                timeApartMethodList.add(nextDurations);
            }
        }
    }
}
