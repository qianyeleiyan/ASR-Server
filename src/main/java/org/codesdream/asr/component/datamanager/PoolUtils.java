package org.codesdream.asr.component.datamanager;

import org.codesdream.asr.model.task.PlanPool;
import org.codesdream.asr.model.task.TaskPool;
import org.codesdream.asr.repository.task.PlanPoolRepository;
import org.codesdream.asr.repository.task.TaskPoolRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class PoolUtils {

    @Resource
    TaskPoolRepository taskPoolRepository;

    @Resource
    PlanPoolRepository planPoolRepository;


    public void deleteTaskInTaskPool(Integer userId, Integer taskId) {
        TaskPool taskPool = taskPoolRepository.findByUserId(userId).get();
        taskPool.getUnableToAppoint().remove(taskId);
        taskPool.getTotallyAppointed().remove(taskId);
        taskPool.getPartlyAppointed().remove(taskId);
    }

    public void deletePlanInPlanPool(Integer userId, Integer planId) {
        PlanPool planPool = planPoolRepository.findByUserId(userId).get();
        planPool.getUnableToAppoint().remove(planId);
        planPool.getTotallyAppointed().remove(planId);
        planPool.getPartlyAppointed().remove(planId);
    }
}
