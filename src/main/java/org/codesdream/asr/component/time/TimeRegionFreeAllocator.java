package org.codesdream.asr.component.time;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.Opt;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class TimeRegionFreeAllocator {

    @Resource
    private WeightToolBox weightToolBox;

    @Resource
    private RegionToolBox regionToolBox;

    @Resource
    private TimeBlockCodeGenerator codeGenerator;


/*    public Map<Integer, TimeBlockInstance> updateTimeRegionMap(Map<Integer, TimeRegion> regionMap, TimeAPMPlan apmPlan){

        for(Map.Entry<Integer, TimeRegion> regionEntry : regionMap.entrySet()){
            TimeRegion timeRegion = regionEntry.getValue();

            if(timeRegion.maxCode >= apmPlan.endCode
                    && timeRegion.minCode <= apmPlan.startCode){
                regionMap.remove(regionEntry.getKey());

                if(timeRegion.minCode < apmPlan.startCode){
                    TimeRegion newRegion = new TimeRegion(codeGenerator);
                    newRegion.minCode = timeRegion.minCode;
                    newRegion.maxCode = codeGenerator.getBefore(apmPlan.startCode);
                    for(TimeBlockInstance timeBlockInstance
                            : getTimeBlockFroRange(timeRegion, newRegion.minCode, newRegion.maxCode).values()){
                        newRegion.timeBlockInstanceMap.put(timeBlockInstance.getCode(), timeBlockInstance);
                    }
                    regionMap.put(newRegion.getMinCode(), newRegion);
                }

                if(timeRegion.maxCode > apmPlan.endCode){
                    TimeRegion newRegion = new TimeRegion(codeGenerator);
                    newRegion.minCode = codeGenerator.getNext(apmPlan.endCode);
                    newRegion.maxCode = timeRegion.maxCode;
                    for(TimeBlockInstance timeBlockInstance
                            : getTimeBlockFroRange(timeRegion, newRegion.minCode, newRegion.maxCode).values()){
                        newRegion.timeBlockInstanceMap.put(timeBlockInstance.getCode(), timeBlockInstance);
                    }
                    regionMap.put(newRegion.getMinCode(), newRegion);
                }

                return getTimeBlockFroRange(timeRegion, apmPlan.startCode, apmPlan.endCode);
            }

        }
        return new HashMap<>();
    }*/

    /**
     * 分配并筛选单次最优方案
     * @param duration 时间段大小
     * @param nextMinCode 开始检测时间
     * @param appointTask 分配任务结构
     * @param region 目标时间段
     * @return 时间区间分配方案
     */
    public Optional<TimeAPMPlan> getSuitableAPMPlan(Integer duration,
                                             Integer nextMinCode,
                                             TimeAppointTask appointTask,
                                             TimeRegion region){

        if(codeGenerator.getDuration(nextMinCode , region.maxCode) < duration) return Optional.empty();

        Map<Integer, Float> weightMap = new HashMap<>();

        // 任务总持续时间（从现在开始）
        int lastNum = codeGenerator.getDuration(codeGenerator.getCurrent(), appointTask.endCode);

        // 从nextMinCode开始继续执行检测
        Calendar calendar = codeGenerator.getCalendarFromCode(nextMinCode);
        for(int i = nextMinCode; i <= region.maxCode; i = codeGenerator.getNext(calendar)){

            // 计算剩余时间
            int lastedTime = codeGenerator.getDuration(i, region.maxCode);

            // 检测接下来是否可分配
            if(lastedTime < duration) break;

            // 用时间区间平均值代替计算
            int code = codeGenerator.getAvgCodeFromDuration(i, duration);

            // 计算偏好时段权重
            float preferTimeWeight = weightToolBox.preferTimeWeight(appointTask.preferenceTime, code);

            // 计算拖延权重
            float earlyLateWeight = weightToolBox.earlyLateWeight( 8, lastNum, code);

            // 计算截止权重
            float deadlineWeight = weightToolBox.deadlineWeight(appointTask.endCode, code);

            // 计算总权重
            float weight = preferTimeWeight * earlyLateWeight + deadlineWeight;

            // 记录权重
            weightMap.put(i, weight);
        }

        // 如果该时间段内无法分配
        if(weightMap.size() == 0) return Optional.empty();

        List<Float> weightList = new ArrayList<>(weightMap.values());

        // 构造分配可能序列
        List<Integer> startCodeList = new ArrayList<>(weightMap.keySet());
        // 排序序列
        Collections.sort(startCodeList);

        // 计算权重均值
        float avgWeight = weightToolBox.calculateAvgWeight(weightList);

        Integer startCode = null;
        Float finalWeight = null;

        // 筛出较优分配方案
        for(Integer code : startCodeList){
            float weight = weightMap.get(code);
            if(weight >= avgWeight){
                startCode = code;
                finalWeight = weight;
                break;
            }
        }

        // 检测是否含有最优方案
        if(startCode == null) return Optional.empty();

        TimeAPMPlan apmPlan = new TimeAPMPlan();

        // 填写分配方案
        apmPlan.duration = duration;
        apmPlan.startCode = startCode;
        apmPlan.endCode =
                codeGenerator.getNextFromDuration(codeGenerator.getCalendarFromCode(startCode), duration);
        apmPlan.Feasibility = finalWeight;

        return Optional.of(apmPlan);
    }


    public Optional<List<TimeAPMPlan>> getAPMPlanMentions(List<TimeRegion> regions,
                                                int nextStartCode,
                                                int duration,
                                                TimeAppointTask appointTask){

        List<TimeAPMPlan> apmPlansMentions = new ArrayList<>();

        // 开始处理
        for (TimeRegion currentRegion : regions) {

            if (currentRegion.maxCode < nextStartCode) continue;

            int realStartCode;

            // 初始化下次开始处理的时间编号
            if (nextStartCode == -1 || nextStartCode < currentRegion.minCode) {
                realStartCode = currentRegion.minCode;
            } else {
                realStartCode = nextStartCode;
            }

            // 在该时间段剩余的可用时间块中尝试分配该时间区间
            Optional<TimeAPMPlan> apmPlan = getSuitableAPMPlan(
                    duration,
                    realStartCode,
                    appointTask,
                    currentRegion);

            // 检查是否可分配
            // 加入最佳时间区间分配方案提名
            apmPlan.ifPresent(apmPlansMentions::add);
        }

        // 如果提名列表不为空则返回
        if(apmPlansMentions.size() > 0)
            return Optional.of(apmPlansMentions);
        else return Optional.empty();
    }

    /**
     * 方案分配
     * @param apartMethod 拆分方案
     * @param availableRegions 作用域内的可用时间段
     * @return
     */
    public Optional<TAMPPlanReport> timeDurationMethodAlloc(TimeApartMethod apartMethod,
                                                  TimeAppointTask appointTask,
                                                  Map<Integer, TimeRegion> availableRegions){

        // 段分配情况列表
        List<TimeAPMPlan> timeAPMPlans = new ArrayList<>();

        // 记录该方案下被分配的时间块
       List<TimeBlockInstance> assignInstances = new ArrayList<>();

        // 创建时间段的顺序数组
        List<TimeRegion> currentRegions = new ArrayList<>(availableRegions.values());
        Collections.sort(currentRegions);

        //下一次开始处理的时间编号
        int nextStartCode = -1;

        // 已经成功分配的时间段编号
        int count = 0;

        // 依次分配时间区间
        for(Integer duration : apartMethod.getDurations()){

            // 执行单时间区间候选分配
            Optional<List<TimeAPMPlan>> optionalTimeAPMPlans =
                    getAPMPlanMentions(currentRegions, nextStartCode, duration, appointTask);

            // 检测是否含有提名
            // 从提名方案中选出较优方案
            if(optionalTimeAPMPlans.isPresent()){

                // 单时间区块分配方案提名列表
                List<TimeAPMPlan> apmPlansMentions = optionalTimeAPMPlans.get();

                // 排序提名方案序列
                Collections.sort(apmPlansMentions);

                // 计算权重均值
                float avgWeight = weightToolBox.calculateAvgWeightForAPMPlan(apmPlansMentions);

                TimeAPMPlan bestAPMPlan = null;

                // 筛出较优分配方案
                for(TimeAPMPlan timeAPMPlan : apmPlansMentions){
                    if(timeAPMPlan.Feasibility >= avgWeight){
                        bestAPMPlan = timeAPMPlan;
                        break;
                    }
                }

                assert bestAPMPlan != null;

                // 去间隔一个的时间块编号作为下一次分配的起始编号
                if(appointTask.mutexPeriod == null || appointTask.type != 1)
                    nextStartCode = codeGenerator.getNext(codeGenerator.getNext(bestAPMPlan.endCode));
                else
                    nextStartCode =
                            codeGenerator.getNextFromDuration(
                                    codeGenerator.getCalendarFromCode(bestAPMPlan.endCode),
                                    appointTask.mutexPeriod);

                TimeRegion targetRegion = null;

                // 查找时间区间分配对应时间段
                for(TimeRegion currentRegion : currentRegions) {
                    if (currentRegion.minCode <= bestAPMPlan.startCode
                            && currentRegion.maxCode >= bestAPMPlan.endCode) {
                        targetRegion = currentRegion;
                        break;
                    }
                }

                assert targetRegion != null;

                // 记录最佳时间区间分配方案
                timeAPMPlans.add(bestAPMPlan);
                // 添加可能受影响的时间块
                assignInstances.addAll(regionToolBox.getInfluencedTimeBlocks(targetRegion, bestAPMPlan));
                // 递增分配成功时间段
                count++;
            }

        }

        // 如果为能分配任何时间段
        if(count == 0) return Optional.empty();

        // 生成报告
        TAMPPlanReport report = new TAMPPlanReport();

        // 设置完全成功分配标志
        report.success = count >= apartMethod.getDurations().size();

        // 填写各个时间区间的分配方案
        report.timeAPMPlans = timeAPMPlans;

        report.weight = weightToolBox.calculateAvgWeightForAPMPlan(timeAPMPlans);

        // 最优方案占用的时间块表
        report.timeBlockInstances = assignInstances;

        return Optional.of(report);

    }

    /**
     * 尝试分配并筛选方案
     * @param appointTask 时间分配任务
     * @param freeRegions 总空闲时间段
     * @return 最优分配方案
     */
    public Optional<TAMPPlanReport> getSuitableMethodPlan(TimeAppointTask appointTask,
                                                          HashMap<Integer, TimeRegion> freeRegions){

        // 执行时间段的作用域处理
        HashMap<Integer, TimeRegion> suitableRegionMap =
                regionToolBox.getSuitableTimeRegionForCalculation(appointTask.endCode, freeRegions);

        List<TAMPPlanReport> tampPlanReports = new ArrayList<>();

        log.info(String.format("Time Apart Method Calculating For RequestId %s Starting.", appointTask.uuid));
        // 依次尝试分配各个分配方案
        for(TimeApartMethod apartMethod : appointTask.timeDurationMethods){

            // 生成满足时长要求的时间段表(检查必要条件是否满足)
            Map<Integer, TimeRegion> availableRegions =
                    regionToolBox.getAvailableRegions(suitableRegionMap, Collections.min(apartMethod.getDurations()));

            if(availableRegions.size() == 0){
                continue;
            }

            log.info(String.format("Time Apart Method Calculation For RequestId %s (%s) Starting.",
                    appointTask.uuid, apartMethod));

            // 执行单个方案分配
            Optional<TAMPPlanReport> tampPlanReport =
                    timeDurationMethodAlloc(apartMethod, appointTask, availableRegions);

            log.info(String.format("Time Apart Method Calculation For RequestId %s (%s) Finished.",
                    appointTask.uuid, apartMethod));

            // 如果方案可行则进行记录
            tampPlanReport.ifPresent(tampPlanReports::add);

        }

        log.info(String.format("Time Apart Method Calculating For RequestId %s Finished.", appointTask.uuid));

        // 如果没有任何解决方案
        if(tampPlanReports.size() == 0) return Optional.empty();


        // 选出最终解决方案
        tampPlanReports.sort(Collections.reverseOrder());

        TAMPPlanReport lastPlanReport = tampPlanReports.get(0);

        // 给出各个时间块对应的分配号
        for(TimeAPMPlan apmPlan : lastPlanReport.timeAPMPlans){
            Calendar calendar = codeGenerator.getCalendarFromCode(apmPlan.startCode);
            for(int i = apmPlan.startCode; i <= apmPlan.endCode; i = codeGenerator.getNext(calendar)){
                lastPlanReport.tbAPMIdMap.put(i, apmPlan.allocId);
            }
        }

        return Optional.of(lastPlanReport);
    }
}
