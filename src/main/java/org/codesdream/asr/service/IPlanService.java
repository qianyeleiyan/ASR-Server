package org.codesdream.asr.service;

import org.codesdream.asr.component.json.model.JsonableCompletion;
import org.codesdream.asr.component.json.model.JsonablePlan;
import org.codesdream.asr.component.json.model.JsonablePlanResult;
import org.codesdream.asr.component.json.model.JsonableResult;
import org.codesdream.asr.model.task.Plan;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IPlanService {

    // 注册计划
    Plan registerPlan(JsonablePlan jsonablePlan);

    // 删除计划
    boolean deletePlan(Integer planId, Integer userId);

    // 获取当前用户名下所有计划
    List<Integer> getPlanIds(Integer userId);

    // 更新计划
    JsonablePlan update(JsonablePlan jsonablePlan);

    // 获取所有用户结果
    JsonableResult getAllResults(Integer userId);

    // 获取单一计划结果
    List<JsonablePlanResult> getPlanResult(Integer userId, Integer planId);

    // 标记计划每一时间段
    boolean markFinished(Integer planId, Date startDate);

    // 获取所有完成的时间段数量
    Map<Integer, Integer> getTotalCompletionCount(Integer userId);

    // 获取上周完成的时间段数量以及总的时间段数量（每天）
    List<JsonableCompletion> getCompletionCountForDays(Integer userId);

    // 获取上周完成的时间段数量以及总的时间段数量（每个计划）
    List<JsonableCompletion> getCompletionCountForPlans(Integer userId);

    // 获取上周已经创建的计划数量
    Integer getPlanCount(Integer userId);
}
