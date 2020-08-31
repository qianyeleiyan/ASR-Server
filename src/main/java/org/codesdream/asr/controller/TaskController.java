package org.codesdream.asr.controller;

import org.codesdream.asr.component.datamanager.JSONParameter;
import org.codesdream.asr.component.json.model.*;
import org.codesdream.asr.component.task.AccomplishmentMarker;
import org.codesdream.asr.component.time.DaysOfLastWeekGetter;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.exception.notfound.TasksNotFound;
import org.codesdream.asr.model.task.Task;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.repository.task.TaskRepository;
import org.codesdream.asr.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private JSONParameter jsonParameter;

    @Resource
    private TaskService taskService;

    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonableTask createTask(@RequestBody JsonableTask jsonableTask, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        jsonableTask.setUserId(user.getId());
        return new JsonableTask(taskService.registerTask(jsonableTask));
    }

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public JsonableTask getTask(@RequestParam Integer id){
        Optional<Task> taskOptional = taskRepository.findById(id);
        if(!taskOptional.isPresent()) throw new NotFoundException(id.toString());
        return new JsonableTask(taskOptional.get());
    }

    @GetMapping("ids")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> getTasksIds(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        return taskService.getTaskIds(user.getId());
    }

    @GetMapping("details")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonableTask> getTasks(@RequestParam(name="ids") List<Integer> ids){
        List<JsonableTask> tasks = new ArrayList<>();
        for(Integer taskId : ids){
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if(!taskOptional.isPresent()) throw new NotFoundException();

            tasks.add(new JsonableTask(taskOptional.get()));
        }
        return tasks;
    }

    @PostMapping(value = "update")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonableTask updateTask(@RequestBody String updaterStr){
        JsonableUpdater updater = jsonParameter.createJsonableUpdater(updaterStr);
        Optional<Task> taskOptional = taskRepository.findById(updater.getId());
        if (!taskOptional.isPresent()) throw new NotFoundException(updater.getId().toString());

        JsonableTask jsonableTask = new JsonableTask(taskOptional.get());

        jsonableTask = jsonParameter.parsePathToObject(updater.getPatch(), jsonableTask);

        return taskService.update(jsonableTask);
    }

    @PostMapping("delete")
    @ResponseStatus(HttpStatus.OK)
    public JsonableDeletion deleteTask(@RequestBody Map<String, Object> taskIdMap, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Integer> taskIds = (List<Integer>) taskIdMap.get("id");
        List<Integer> successfulDeletion = new ArrayList<>();
        List<Integer> failedDeletion = new ArrayList<>();
        boolean flag;
        for (Integer taskId : taskIds) {
            if (taskService.deleteTask(taskId, user.getId())) {
                successfulDeletion.add(taskId);
            } else {
                failedDeletion.add(taskId);
            }
        }
        if (successfulDeletion.size() == taskIds.size()) flag = true;
        else flag = false;
        return new JsonableDeletion(successfulDeletion, failedDeletion, flag);
    }

    @GetMapping("results")
    @ResponseStatus(HttpStatus.OK)
    public JsonableResult getResults(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return taskService.getAllResults(user.getId());
    }

    @GetMapping("today")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonableTaskResult> getTaskForToday(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        JsonableResult jsonableResult = taskService.getAllResults(user.getId());
        if (jsonableResult == null) {
            throw new TasksNotFound("No task registered for current user.");
        }
        List<JsonableTaskResult> jsonableTaskResults = jsonableResult.getTaskResultList();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = format.format(today);
        List<JsonableTaskResult> taskResultsForToday = new ArrayList<>();
        for (JsonableTaskResult jsonableTaskResult : jsonableTaskResults) {
            String beginString = format.format(jsonableTaskResult.getBegin());
            if (today.before(jsonableTaskResult.getEnd())) {
                if (beginString.equals(todayString)) {
                    taskResultsForToday.add(jsonableTaskResult);
                } else {
                    break;
                }
            }
        }
        return taskResultsForToday;
    }

    @PostMapping("mark")
    @ResponseStatus(HttpStatus.OK)
    public boolean markTaskFinished(@RequestBody AccomplishmentMarker accomplishmentMarker) {
        Integer taskId = accomplishmentMarker.getId();
        Date start = accomplishmentMarker.getDate();
        return taskService.markFinished(taskId, start);
    }

    @GetMapping("completion/total")
    @ResponseStatus(HttpStatus.OK)
    public Map<Integer, Integer> getTotalCompletionCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return taskService.getTotalCompletionCount(user.getId());
    }

    @GetMapping("completion/per_day")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonableCompletion> getCompletionPerDayForLastWeek(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return taskService.getCompletionCountForDays(user.getId());
    }

    @GetMapping("completion/per_task")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonableCompletion> getComoletoinPerTaskForLastWeek(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return taskService.getCompletionCountForTasks(user.getId());
    }

    @GetMapping("creation_count/last_week")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCountOfTaskCreationForLastWeek(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return taskService.getTaskCount(user.getId());
    }

    @GetMapping("test")
    @ResponseStatus(HttpStatus.OK)
    public boolean test() {
        List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
        Date beginOfLastWeek = periodOfLastWeek.get(0);
        Date endOfLastWeek = periodOfLastWeek.get(1);
        System.out.println(beginOfLastWeek.toString());
        System.out.println(endOfLastWeek.toString());
        return true;
    }
}
