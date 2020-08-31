package org.codesdream.asr.service;

import org.codesdream.asr.component.json.model.JsonableCompletion;
import org.codesdream.asr.component.json.model.JsonableResult;
import org.codesdream.asr.component.json.model.JsonableTask;
import org.codesdream.asr.component.json.model.JsonableTaskResult;
import org.codesdream.asr.model.task.Task;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ITaskService {

    // 注册任务
    Task registerTask(JsonableTask jsonableTask);

    // 删除任务
    boolean deleteTask(Integer taskId, Integer userId);

    // 获取当前用户名下所有任务
    List<Integer> getTaskIds(Integer userId);

    // 更新任务
    JsonableTask update(JsonableTask jsonableTask);

    // 获取所有用户结果
    JsonableResult getAllResults(Integer userId);

    // 获取单一任务结果
    List<JsonableTaskResult> getTaskResult(Integer userId, Integer taskId);

    // 标记任务每一时间段
    boolean markFinished(Integer taskId, Date startDate);

    // 获取所有完成的时间段数量
    Map<Integer, Integer> getTotalCompletionCount(Integer userId);

    // 获取上周完成的时间段数量以及总的时间段数量（每天）
    List<JsonableCompletion> getCompletionCountForDays(Integer userId);

    // 获取上周完成的时间段数量以及总的时间段数量（每个任务）
    List<JsonableCompletion> getCompletionCountForTasks(Integer userId);

    // 获取上周已经创建的任务数量
    Integer getTaskCount(Integer userId);

}
