package org.codesdream.asr.service;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.datamanager.AppointmentGenerator;
import org.codesdream.asr.component.datamanager.DescriptionGenerator;
import org.codesdream.asr.component.datamanager.JsonableResultGenerator;
import org.codesdream.asr.component.datamanager.PoolUtils;
import org.codesdream.asr.component.json.model.*;
import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.component.task.TaskRequest;
import org.codesdream.asr.component.time.DaysOfLastWeekGetter;
import org.codesdream.asr.component.time.TimeBlockCodeGenerator;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.model.task.Task;
import org.codesdream.asr.model.task.TaskPool;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.codesdream.asr.repository.task.TaskPoolRepository;
import org.codesdream.asr.repository.task.TaskRepository;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.codesdream.asr.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class TaskService implements ITaskService {

    @Resource
    TaskRepository taskRepository;

    @Resource
    ITimeService timeService;

    @Resource
    UserRepository userRepository;

    @Resource
    DescriptionGenerator descriptionGenerator;

    TaskRequest taskRequest = TaskRequest.getInstance();

    @Resource
    JsonableResultGenerator jsonableResultGenerator;

    @Resource
    TaskPoolRepository taskPoolRepository;

    @Resource
    TimeAPMAllocRepository timeAPMAllocRepository;

    @Resource
    TimeBlockCodeGenerator timeBlockCodeGenerator;

    @Resource
    AppointmentGenerator appointmentGenerator;

    @Resource
    PoolUtils poolUtils;

    @Override
    public Task registerTask(JsonableTask jsonableTask) {
        Task task = new Task();
        task.setDeadline(jsonableTask.getDeadline());
        task.setDescription(descriptionGenerator.getString(jsonableTask.getDescription()));
        task.setImportantDegree(jsonableTask.getImportantDegree());
        task.setSingleMin(jsonableTask.getSingleMin());
        task.setPreference(jsonableTask.getPreference());
        task.setSingleMax(jsonableTask.getSingleMax());
        task.setUserId(jsonableTask.getUserId());
        task.setDuration(jsonableTask.getDuration());
        task.setUrgencyPreference(jsonableTask.getUrgencyPreference());
        task = taskRepository.save(task);
        TaskPool taskPool = taskPoolRepository.findByUserId(jsonableTask.getUserId()).get();
        if (allocateTask(task.getId())) {
            taskPool.getPartlyAppointed().add(task.getId());
        } else {
            taskPool.getUnableToAppoint().add(task.getId());
        }
        return task;
    }

    @Override
    public boolean deleteTask(Integer taskId, Integer userId) {

        Optional<Task> OTask = taskRepository.findById(taskId);
        if (!OTask.isPresent()) return false;
        Task task = OTask.get();
        List<String> allocatedNumbers = task.getAllocationNumbers();
        for (String number : allocatedNumbers) {
            timeService.rollbackAllocTask(task.getUserId(), number);
        }
        poolUtils.deleteTaskInTaskPool(userId, taskId);
        for (String allocNumber : task.getAllocationNumbers()) {
            timeService.rollbackAllocTask(userId, allocNumber);
        }
        taskRepository.delete(task);
        return true;
    }

    @Override
    public List<Integer> getTaskIds(Integer userId) {
        try {
            if (!userRepository.findById(userId).isPresent()) {
                throw new InnerDataTransmissionException("User not exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterable<Task> tasks = taskRepository.findAllByUserId(userId);
        List<Integer> taskIds = new ArrayList<>();
        for (Task task : tasks) {
            taskIds.add(task.getId());
        }
        return taskIds;
    }

    @Override
    public JsonableTask update(JsonableTask jsonableTask) {
        Integer taskId = jsonableTask.getId();
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (!taskOptional.isPresent()) {
            throw new NotFoundException("Task not found.");
        }
        Task task = taskOptional.get();
        if (task.getDuration() - task.getAllocatedDuration() < 0) {
            throw new InnerDataTransmissionException("Illegal duration set. Illegal data setting, " +
                    "the time required for the task must be greater than the completed time");
        }
        Date now = new Date();
        for (String allocationNumber : task.getAllocationNumbers()) {
            Optional<TimeAPMAlloc> timeAPMAllocOptional = timeAPMAllocRepository.findByApmId(allocationNumber);
            if (!timeAPMAllocOptional.isPresent()) {
                throw new NotFoundException("TimeAPMAlloc not found.");
            }
            TimeAPMAlloc timeAPMAlloc = timeAPMAllocOptional.get();
            Date startDate = timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode());
            if (startDate.before(now)) continue;
            timeService.rollbackAllocTask(task.getUserId(), allocationNumber);
        }
        taskRepository.save(jsonableTask.parseModel());
        allocateTask(taskId);
        return jsonableTask;

    }

    @Override
    public JsonableResult getAllResults(Integer userId) {
        List<Integer> taskIds = getTaskIds(userId);
        List<Integer> failedIds = new ArrayList<>();
        List<JsonableTaskResult> jsonableTaskResultList = new ArrayList<>();
        if (taskIds.isEmpty()) {
            return null;
        }
        for (Integer taskId : taskIds) {
            List<JsonableTaskResult> jsonableTaskResults = getTaskResult(userId, taskId);
            if (jsonableTaskResults == null || jsonableTaskResults.isEmpty()) {
                failedIds.add(taskId);
                continue;
            }
            jsonableTaskResultList.addAll(jsonableTaskResults);
        }
        Collections.sort(jsonableTaskResultList);
        return new JsonableResult(jsonableTaskResultList, null, failedIds);
    }

    @Override
    public List<JsonableTaskResult> getTaskResult(Integer userId, Integer taskId) {
        Optional<Task> OTask = taskRepository.findById(taskId);
        if (!OTask.isPresent()) {
            try {
                throw new InnerDataTransmissionException("Task not exists.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Task task = OTask.get();
        Map<Integer, List<String>> taskRequest1 = taskRequest.getTaskRequest();
        Set<Integer> idSet = taskRequest1.keySet();
        Integer totalDuration = 0;
        List<JsonableTaskResult> jsonableTaskResults = new ArrayList<>();

        for (String allocationNumber : task.getAllocationNumbers()) {
            Optional<TimeAPMAlloc> OTimeAMPAlloc = timeAPMAllocRepository.findByApmId(allocationNumber);
            if (!OTimeAMPAlloc.isPresent()) {
                throw new NotFoundException("TimeAMPAlloc not found.");
            }
            TimeAPMAlloc timeAPMAlloc = OTimeAMPAlloc.get();
            int status = PlanService.statusChecker(timeAPMAlloc, task.getFinishedAllocation(),
                    timeBlockCodeGenerator);
            jsonableResultGenerator.generateJsonableTaskResults(taskId, jsonableTaskResults, timeAPMAlloc, status);
        }
        Collections.sort(jsonableTaskResults);
        if (idSet.contains(taskId)) {
            List<String> requestNumbers = taskRequest1.get(taskId);
            List<String> toDelete = new ArrayList<>();
            int failedRequest = 0;
            for (String requestNumber : requestNumbers) {
                List<TimeAPMAlloc> timeAPMAllocList = timeService.getAllocTaskResult(userId, requestNumber);
                if (timeAPMAllocList == null || timeAPMAllocList.isEmpty()) {
                    failedRequest++;
                    continue;
                }
                toDelete.add(requestNumber);
                Collections.sort(timeAPMAllocList);
                for (TimeAPMAlloc timeAPMAlloc : timeAPMAllocList) {
                    int status = PlanService.statusChecker(timeAPMAlloc, task.getFinishedAllocation(),
                            timeBlockCodeGenerator);
                    jsonableResultGenerator.generateJsonableTaskResults(taskId, jsonableTaskResults, timeAPMAlloc, status);
                    task.getAllocationNumbers().add(timeAPMAlloc.getApmId());
                    totalDuration += timeAPMAlloc.getDuration();
                }
            }
            TaskPool taskPool = taskPoolRepository.findByUserId(userId).get();
            if (failedRequest == requestNumbers.size()) {
                taskPool.getUnableToAppoint().add(taskId);
                return new ArrayList<>();
            } else {
                requestNumbers.removeAll(toDelete);
                if (requestNumbers.isEmpty()) {
                    taskRequest1.remove(taskId);
                } else {
                    taskRequest1.put(taskId, requestNumbers);
                }
                taskRequest.setTaskRequest(taskRequest1);
                task.setAllocatedDuration(task.getAllocatedDuration() + totalDuration);
                if (task.getAllocatedDuration().equals(task.getDuration())) {
                    poolUtils.deleteTaskInTaskPool(userId, taskId);
                    task.setIsCompleted(true);
                    taskPool.getTotallyAppointed().add(task.getId());
                }
                taskRepository.save(task);
                Collections.sort(jsonableTaskResults);
            }
        }
        return jsonableTaskResults;
    }

    @Override
    public boolean markFinished(Integer taskId, Date startDate) {
        Optional<Task> OTask = taskRepository.findById(taskId);
        if (!OTask.isPresent()) {
            throw new NotFoundException("Task not exists.");
        }
        Task task = OTask.get();
        int index = searchStartDateInAllocations(task, startDate);
        if (index >= task.getAllocationNumbers().size() || index < 0) {
            return false;
        }
        String allocationNumber = task.getAllocationNumbers().get(index);
        Optional<TimeAPMAlloc> OTimeAPMAlloc = timeAPMAllocRepository.findByApmId(allocationNumber);
        if (!OTimeAPMAlloc.isPresent()) {
            throw new NotFoundException("TimeAPMAlloc not found.");
        }
        TimeAPMAlloc timeAPMAlloc = OTimeAPMAlloc.get();

        if (!timeAPMAlloc.getStartCode().equals(timeBlockCodeGenerator.get(startDate))) {
            return false;
        }

        // task.getAllocationNumbers().remove(index);
        task.setFinishedDuration(task.getFinishedDuration() + timeAPMAlloc.getDuration());
        task.getFinishedAllocation().add(allocationNumber);
        if (task.getFinishedDuration().equals(task.getDuration())) {
            task.setIsCompleted(true);
        }
        task.getMarkCount().incrementAndGet();
        taskRepository.save(task);
        return true;
    }

    @Override
    public Map<Integer, Integer> getTotalCompletionCount(Integer userId) {
        Map<Integer, Integer> result = new HashMap<>();
        List<Integer> taskIds = getTaskIds(userId);
        for (Integer taskId : taskIds) {
            Optional<Task> OTask = taskRepository.findById(taskId);
            if (!OTask.isPresent()) {
               continue;
            }
            Task task = OTask.get();
            result.put(task.getId(), task.getMarkCount().get());
        }
        return result;
    }

    @Override
    public List<JsonableCompletion> getCompletionCountForDays(Integer userId) {
        List<Integer> taskIds = getTaskIds(userId);
        int[] total = new int[8];
        int[] finished = new int[8];
        List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
        Date beginOfLastWeek = periodOfLastWeek.get(0);
        Date endOfLastWeek = periodOfLastWeek.get(1);
        for (Integer taskId : taskIds) {
            Optional<Task> OTask = taskRepository.findById(taskId);
            if (!OTask.isPresent()) {
               continue;
            }
            Task task = OTask.get();
            List<String> allAllocations = task.getAllocationNumbers();
            List<String> finishedAllocations = task.getFinishedAllocation();
            for (String allocationNumber : allAllocations) {
                Optional<TimeAPMAlloc> OTimeAPMAlloc = timeAPMAllocRepository.findByApmId(allocationNumber);
                if (!OTimeAPMAlloc.isPresent()) {
                    try {
                        throw new NotFoundException("TimeAPMAlloc not found.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                TimeAPMAlloc timeAPMAlloc = OTimeAPMAlloc.get();
                Date startDate = timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode());
                if (startDate.after(beginOfLastWeek) && startDate.before(endOfLastWeek)) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDate);
                    int index = calendar.get(Calendar.DAY_OF_WEEK);
                    if (index == Calendar.SUNDAY) {
                        total[7]++;
                    } else {
                        total[index - 1]++;
                    }
                    if (finishedAllocations.contains(allocationNumber)) {
                        if (index == Calendar.SUNDAY) {
                            finished[7]++;
                        } else {
                            finished[index - 1]++;
                        }
                    }
                }
            }
        }
        List<JsonableCompletion> jsonableCompletionList = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            jsonableCompletionList.add(new JsonableCompletionPerDay(i, finished[i], total[i]));
        }
        return jsonableCompletionList;
    }

    @Override
    public List<JsonableCompletion> getCompletionCountForTasks(Integer userId) {
        List<Integer> taskIds = getTaskIds(userId);
        List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
        Date beginOfLastWeek = periodOfLastWeek.get(0);
        Date endOfLastWeek = periodOfLastWeek.get(1);
        List<JsonableCompletion> jsonableCompletionList = new ArrayList<>();
        for (Integer taskId : taskIds) {
            int total = 0;
            int finished = 0;
            Optional<Task> OTask = taskRepository.findById(taskId);
            if (!OTask.isPresent()) {
                continue;
            }
            Task task = OTask.get();
            List<String> allAllocations = task.getAllocationNumbers();
            List<String> finishedAllocations = task.getFinishedAllocation();
            for (String allocationNumber : allAllocations) {
                Optional<TimeAPMAlloc> OTimeAPMAlloc = timeAPMAllocRepository.findByApmId(allocationNumber);
                if (!OTimeAPMAlloc.isPresent()) {
                    try {
                        throw new NotFoundException("TimeAPMAlloc not found.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                TimeAPMAlloc timeAPMAlloc = OTimeAPMAlloc.get();
                Date startDate = timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode());
                if (startDate.after(beginOfLastWeek) && startDate.before(endOfLastWeek)) {
                    total++;
                    if (finishedAllocations.contains(allocationNumber)) {
                        finished++;
                    }
                }
            }
            jsonableCompletionList.add(new JsonableCompletionPerTask(taskId, finished, total));
        }
        return jsonableCompletionList;
    }

    @Override
    public Integer getTaskCount(Integer userId) {
        int cnt = 0;
        List<Integer> taskIds = getTaskIds(userId);
        for (Integer taskId : taskIds) {
            Optional<Task> OTask = taskRepository.findById(taskId);
            if (!OTask.isPresent()) {
                continue;
            }
            Task task = OTask.get();
            Date createDate = task.getCreateDate();
            List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
            Date beginOfLastWeek = periodOfLastWeek.get(0);
            Date endOfLastWeek = periodOfLastWeek.get(1);
            if (createDate.after(beginOfLastWeek) && createDate.before(endOfLastWeek)) {
                cnt++;
            }
        }
        return cnt;
    }


    private boolean allocateTask(Integer taskId) {
        Optional<Task> OTask = taskRepository.findById(taskId);
        if (!OTask.isPresent()) {
            throw new InnerDataTransmissionException("Task not exists.");
        }
        Task task = OTask.get();
        Integer duration = task.getDuration() - task.getAllocatedDuration();
        if (duration == 0) {
            task.setIsCompleted(true);
            return true;
        }
        Appointment appointment = appointmentGenerator.convertToAppointment(task);
        log.info(appointment.toString());
        String requestNumber = timeService.createAllocTask(task.getUserId(), appointment);
        if (requestNumber == null) {
            return false;
        }
        log.info(taskId + ": " + requestNumber);
        if (taskRequest.getTaskRequest().containsKey(taskId)) {
            taskRequest.getTaskRequest().get(taskId).add(requestNumber);
        } else {
            taskRequest.getTaskRequest().put(taskId, new ArrayList<>(Arrays.asList(requestNumber)));
        }
        return true;
    }

    private int searchStartDateInAllocations(Task task, Date date) {
        Integer code = timeBlockCodeGenerator.get(date);
        List<String> allocationNumbers = task.getAllocationNumbers();
        int l = 0, r = allocationNumbers.size();
        while (l < r) {
            int mid = (l + r) >> 1;
            TimeAPMAlloc timeAPMAlloc = timeAPMAllocRepository.findByApmId(allocationNumbers.get(mid)).get();
            int startCode = timeAPMAlloc.getStartCode();
            if (startCode >= code) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return l;
    }

}
