package org.codesdream.asr.component.time;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.slf4j.Logger;

import java.io.Serializable;
import java.util.*;

@Data
public class TATaskProcessor implements Serializable {

    private final Integer userId;

    private transient TimeRegionsPool regionsPool;

    private transient TimeBlocksPool timeBlocksPool;

    private final Map<String, TimeAppointTask> appointTaskMap = new HashMap<>();

    private final List<String> finishedAppointTasks = new ArrayList<>();

    // 需要手动注入的依赖
    private transient TimeRegionFreeAllocator regionAllocator;

    private transient TimeAPMAllocRepository apmAllocRepository;

    private transient TimeSystemEngine timeSystemEngine;

    private transient TimeBlockCodeGenerator codeGenerator;

    private transient RegionToolBox regionToolBox;

    private Logger log;

    public TATaskProcessor(Integer userId, TimeRegionsPool regionsPool){
        this.userId = userId;
        this.regionsPool = regionsPool;
        this.timeBlocksPool = regionsPool.getTimeBlocksPool();

    }

    public void registerTimeAppointTask(TimeAppointTask appointTask){
        appointTaskMap.put(appointTask.uuid, appointTask);
        log.info(String.format("Start Register Task Appoint Task TO TimeSystemEngine: %s ...", appointTask.uuid));
        timeSystemEngine.registerTimeAllocTask(userId, appointTask.uuid);
    }

    /**
     * 执行时间分配任务
     * @param requestId 分配号
     */
    public synchronized boolean runAllocTask(String requestId){

        log.info(String.format("Time Alloc Task For RequestId %s Start.", requestId));

        // 获得分配号
        TimeAppointTask appointTask = appointTaskMap.get(requestId);

        // 执行检查
        if(appointTask == null) throw new NotFoundException(requestId);

        Optional<TAMPPlanReport> planReportOptional = Optional.empty();

        // 任务
        if(appointTask.type == 0) {
            log.info(String.format("Time Duration Method Processing For RequestId %s Type %d Start.",
                    requestId, appointTask.type));
            // 生成分配方案列表
            regionToolBox.durationApart(appointTask);
        }
        else if(appointTask.type == 1){
            log.info(String.format("Time Duration Method Processing For RequestId %s Type %d Start.",
                    requestId, appointTask.type));
            // 生成分配方案列表
            regionToolBox.planTAMListMaker(appointTask);
        }

        log.info(String.format("Time Duration Method Processing For RequestId %s Done.", requestId));

        assert regionAllocator != null;

        log.info(String.format("Time Alloc Task Processing For RequestId %s Starting...", requestId));
        // 执行空闲时间分配函数组
        planReportOptional =
                regionAllocator.getSuitableMethodPlan(appointTask, new HashMap<>(regionsPool.getFreeRegions()));

        log.info(String.format("Time Alloc Task Processing For RequestId %s Done.", requestId));

        assert planReportOptional != null;

        // 如果执行成功
        if(planReportOptional.isPresent()) {
            log.info(String.format("Time Alloc Task For RequestId %s Succeed.", requestId));
            // 整理时间池
            updateTimeInfo(appointTask, planReportOptional.get());
            // 整理已分配时间段
            regionsPool.mergeAssigned();
        }
        else{
            log.error(String.format("Time Alloc Task For RequestId %s Failed.", requestId));
            onlySetTATFinished(appointTask);
        }

        log.info(String.format("Time Alloc Task For RequestId %s Finished.", requestId));

        return true;

    }

    /**
     * 整理时间池（未分配时间段）
     * @param regionsPool 时间段池
     * @param apmPlanList 时间分配方案列表
     */
    private void setFreeRegions(TimeRegionsPool regionsPool, List<TimeAPMPlan> apmPlanList){

        // 遍历时间分配方案，对现有的空闲时间段处理
        for(TimeAPMPlan apmPlan : apmPlanList){
            regionsPool.assignTimeRegion(apmPlan.startCode, apmPlan.endCode, apmPlan.allocId);
        }
    }

    private void onlySetTATFinished(TimeAppointTask appointTask){

        // 设置时间分配任务的成功状态为失败
        appointTask.success = false;
        // 标记任务已完成
        finishedAppointTasks.add(appointTask.uuid);
        // 无方案
        appointTask.apmPlans = new ArrayList<>();
    }

    /**
     * 根据分配方案刷新时间段池结构
     * @param appointTask 时间分配任务
     * @param planReport 时间分配报告
     */
    private void updateTimeInfo(TimeAppointTask appointTask, TAMPPlanReport planReport){

        appointTask.apmPlans = planReport.timeAPMPlans;

        // 更新时间段池的空闲时间段
        setFreeRegions(regionsPool, planReport.timeAPMPlans);

        // 设置时间分配任务的成功状态
        appointTask.success = true;
        appointTask.half_success = planReport.success;

        // 标记任务已完成
        finishedAppointTasks.add(appointTask.uuid);

        // 在数据库中写入时间分配方案的分配信息
        for(TimeAPMPlan apmPlan : planReport.timeAPMPlans){
            log.info(String.format("Writing APMPlan For %d RequestId %s To Database: (%d,%d) %s",
                    userId, appointTask.uuid, apmPlan.startCode, apmPlan.endCode, apmPlan.allocId));

            TimeAPMAlloc apmAlloc = apmPlan.parseTimeAPMAlloc();
            apmAlloc.setRequestId(appointTask.uuid);
            apmAllocRepository.save(apmAlloc);
        }

    }

    public void processResult(){

    }

}
