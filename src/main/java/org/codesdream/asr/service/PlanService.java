package org.codesdream.asr.service;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.datamanager.AppointmentGenerator;
import org.codesdream.asr.component.datamanager.DescriptionGenerator;
import org.codesdream.asr.component.datamanager.JsonableResultGenerator;
import org.codesdream.asr.component.datamanager.PoolUtils;
import org.codesdream.asr.component.json.model.*;
import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.component.task.PlanRequest;
import org.codesdream.asr.component.time.DaysOfLastWeekGetter;
import org.codesdream.asr.component.time.TimeBlockCodeGenerator;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.model.task.Plan;
import org.codesdream.asr.model.task.PlanPool;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.codesdream.asr.repository.task.PlanPoolRepository;
import org.codesdream.asr.repository.task.PlanRepository;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.codesdream.asr.repository.user.UserRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class PlanService implements IPlanService {

    @Resource
    PlanRepository planRepository;

    @Resource
    DescriptionGenerator descriptionGenerator;

    @Resource
    PlanPoolRepository planPoolRepository;

    @Resource
    ITimeService timeService;

    PlanRequest planRequest = PlanRequest.getInstance();

    @Resource
    PoolUtils poolUtils;

    @Resource
    UserRepository userRepository;

    @Resource
    TimeAPMAllocRepository timeAPMAllocRepository;

    @Resource
    TimeBlockCodeGenerator timeBlockCodeGenerator;

    @Resource
    AppointmentGenerator appointmentGenerator;

    @Resource
    JsonableResultGenerator jsonableResultGenerator;

    @Override
    public Plan registerPlan(JsonablePlan jsonablePlan) {
        Plan plan = new Plan();
        plan.setDeadline(jsonablePlan.getDeadline());
        plan.setDescription(descriptionGenerator.getString(jsonablePlan.getDescription()));
        plan.setImportantDegree(jsonablePlan.getImportantDegree());
        plan.setSingleMin(jsonablePlan.getSingleMin());
        plan.setPreference(jsonablePlan.getPreference());
        plan.setSingleMax(jsonablePlan.getSingleMin());
        plan.setUserId(jsonablePlan.getUserId());
        plan.setDuration(jsonablePlan.getDuration());
        plan.setMutexPeriod(jsonablePlan.getMutexPeriod());
        if (jsonablePlan.getUrgencyPreference() != null) {
            plan.setUrgencyPreference(jsonablePlan.getUrgencyPreference());
        }
        plan = planRepository.save(plan);
        PlanPool planPool = planPoolRepository.findByUserId(jsonablePlan.getUserId()).get();
        if (allocatePlan(plan.getId())) {
            planPool.getPartlyAppointed().add(plan.getId());
        } else {
            planPool.getUnableToAppoint().add(plan.getId());
        }
        return plan;
    }

    @Override
    public boolean deletePlan(Integer planId, Integer userId) {

        Optional<Plan> OPlan = planRepository.findById(planId);
        if (!OPlan.isPresent()) return false;
        Plan plan = OPlan.get();
        List<String> allocatedNumbers = plan.getAllocationNumbers();
        for (String number : allocatedNumbers) {
            timeService.rollbackAllocTask(plan.getUserId(), number);
        }
        poolUtils.deletePlanInPlanPool(userId, planId);
        for (String allocNumber : plan.getAllocationNumbers()) {
            timeService.rollbackAllocTask(userId, allocNumber);
        }
        planRepository.delete(plan);
        return true;
    }

    @Override
    public List<Integer> getPlanIds(Integer userId) {
        try {
            if (!userRepository.findById(userId).isPresent()) {
                throw new InnerDataTransmissionException("User not exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Iterable<Plan> plans = planRepository.findAllByUserId(userId);
        List<Integer> planIds = new ArrayList<>();
        for (Plan plan : plans) {
            planIds.add(plan.getId());
        }
        return planIds;
    }

    @Override
    public JsonablePlan update(JsonablePlan jsonablePlan) {
        Integer planId = jsonablePlan.getId();
        Optional<Plan> planOptional = planRepository.findById(planId);
        if (!planOptional.isPresent()) {
            throw new NotFoundException("Plan not found.");
        }
        Plan plan = planOptional.get();
        if (plan.getDuration() - plan.getAllocatedDuration() < 0) {
            throw new InnerDataTransmissionException("Illegal duration set. Illegal data setting, " +
                    "the time required for the plan must be greater than the completed time");
        }
        Date now = new Date();
        for (String allocationNumber : plan.getAllocationNumbers()) {
            Optional<TimeAPMAlloc> timeAPMAllocOptional = timeAPMAllocRepository.findByApmId(allocationNumber);
            if (!timeAPMAllocOptional.isPresent()) {
                throw new NotFoundException("TimeAPMAlloc not found.");
            }
            TimeAPMAlloc timeAPMAlloc = timeAPMAllocOptional.get();
            Date startDate = timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode());
            if (startDate.before(now)) continue;
            timeService.rollbackAllocTask(plan.getUserId(), allocationNumber);
        }
        planRepository.save(jsonablePlan.parseModel());
        allocatePlan(planId);
        return jsonablePlan;

    }

    @Override
    public JsonableResult getAllResults(Integer userId) {
        List<Integer> planIds = getPlanIds(userId);
        List<Integer> failedIds = new ArrayList<>();
        List<JsonablePlanResult> jsonablePlanResultList = new ArrayList<>();
        if (planIds.isEmpty()) {
            return null;
        }
        for (Integer planId : planIds) {
            List<JsonablePlanResult> jsonablePlanResults = getPlanResult(userId, planId);
            if (jsonablePlanResults == null || jsonablePlanResults.isEmpty()) {
                failedIds.add(planId);
                continue;
            }
            jsonablePlanResultList.addAll(jsonablePlanResults);
        }
        Collections.sort(jsonablePlanResultList);
        return new JsonableResult(null, jsonablePlanResultList, failedIds);
    }

    @Override
    public List<JsonablePlanResult> getPlanResult(Integer userId, Integer planId) {
        Optional<Plan> OPlan = planRepository.findById(planId);
        if (!OPlan.isPresent()) {
            try {
                throw new InnerDataTransmissionException("Plan not exists.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Plan plan = OPlan.get();
        Map<Integer, List<String>> planRequest1 = planRequest.getPlanRequest();
        Set<Integer> idSet = planRequest1.keySet();
        Integer totalDuration = 0;
        List<JsonablePlanResult> jsonablePlanResults = new ArrayList<>();

        for (String allocationNumber : plan.getAllocationNumbers()) {
            Optional<TimeAPMAlloc> OTimeAMPAlloc = timeAPMAllocRepository.findByApmId(allocationNumber);
            if (!OTimeAMPAlloc.isPresent()) {
                throw new NotFoundException("TimeAMPAlloc not found.");
            }
            TimeAPMAlloc timeAPMAlloc = OTimeAMPAlloc.get();
            int status = statusChecker(timeAPMAlloc, plan.getFinishedAllocation(), timeBlockCodeGenerator);
            jsonableResultGenerator.generateJsonablePlanResults(planId, jsonablePlanResults, timeAPMAlloc, status);
        }
        Collections.sort(jsonablePlanResults);
        if (idSet.contains(planId)) {
            List<String> requestNumbers = planRequest1.get(planId);
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
                    int status = statusChecker(timeAPMAlloc, plan.getFinishedAllocation(), timeBlockCodeGenerator);
                    jsonableResultGenerator.generateJsonablePlanResults(planId, jsonablePlanResults, timeAPMAlloc, status);
                    plan.getAllocationNumbers().add(timeAPMAlloc.getApmId());
                    totalDuration += timeAPMAlloc.getDuration();
                }
            }
            PlanPool planPool = planPoolRepository.findByUserId(userId).get();
            if (failedRequest == requestNumbers.size()) {
                planPool.getUnableToAppoint().add(planId);
                return new ArrayList<>();
            } else {
                requestNumbers.removeAll(toDelete);
                if (requestNumbers.isEmpty()) {
                    planRequest1.remove(planId);
                } else {
                    planRequest1.put(planId, requestNumbers);
                }
                planRequest.setPlanRequest(planRequest1);
                plan.setAllocatedDuration(plan.getAllocatedDuration() + totalDuration);
                if (plan.getAllocatedDuration().equals(plan.getDuration())) {
                    poolUtils.deletePlanInPlanPool(userId, planId);
                    plan.setIsCompleted(true);
                    planPool.getTotallyAppointed().add(plan.getId());
                }
                planRepository.save(plan);
                Collections.sort(jsonablePlanResults);
            }
        }
        return jsonablePlanResults;
    }

    static int statusChecker(TimeAPMAlloc timeAPMAlloc, List<String> finishedAllocation,
                             TimeBlockCodeGenerator timeBlockCodeGenerator) {
        int status = 0;
        if (finishedAllocation.contains(timeAPMAlloc.getApmId())) {
            status = 1;
        } else {
            Date now = new Date();
            Date endDate = timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endDate);
            calendar.add(Calendar.MINUTE, timeAPMAlloc.getDuration() * 30);
            if (endDate.before(now)) {
                status = 2;
            }
        }
        return status;
    }

    @Override
    public boolean markFinished(Integer planId, Date startDate) {
        Optional<Plan> OPlan = planRepository.findById(planId);
        if (!OPlan.isPresent()) {
            throw new NotFoundException("Plan not exists.");
        }
        Plan plan = OPlan.get();
        int index = searchStartDateInAllocations(plan, startDate);
        if (index >= plan.getAllocationNumbers().size() || index < 0) {
            return false;
        }
        String allocationNumber = plan.getAllocationNumbers().get(index);
        Optional<TimeAPMAlloc> OTimeAPMAlloc = timeAPMAllocRepository.findByApmId(allocationNumber);
        if (!OTimeAPMAlloc.isPresent()) {
            throw new NotFoundException("TimeAPMAlloc not found.");
        }
        TimeAPMAlloc timeAPMAlloc = OTimeAPMAlloc.get();

        if (!timeAPMAlloc.getStartCode().equals(timeBlockCodeGenerator.get(startDate))) {
            return false;
        }

        plan.getAllocationNumbers().remove(index);
        plan.setFinishedDuration(plan.getFinishedDuration() + timeAPMAlloc.getDuration());
        plan.getFinishedAllocation().add(allocationNumber);
        if (plan.getFinishedDuration().equals(plan.getDuration())) {
            plan.setIsCompleted(true);
        }
        plan.getMarkCount().incrementAndGet();
        planRepository.save(plan);
        return true;
    }

    @Override
    public Map<Integer, Integer> getTotalCompletionCount(Integer userId) {
        Map<Integer, Integer> result = new HashMap<>();
        List<Integer> planIds = getPlanIds(userId);
        for (Integer planId : planIds) {
            Optional<Plan> OPlan = planRepository.findById(planId);
            if (!OPlan.isPresent()) {
                throw new NotFoundException("Plan not found.");
            }
            Plan plan = OPlan.get();
            result.put(plan.getId(), plan.getMarkCount().get());
        }
        return result;
    }

    @Override
    public List<JsonableCompletion> getCompletionCountForDays(Integer userId) {
        List<Integer> planIds = getPlanIds(userId);
        int[] total = new int[8];
        int[] finished = new int[8];
        List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
        Date beginOfLastWeek = periodOfLastWeek.get(0);
        Date endOfLastWeek = periodOfLastWeek.get(1);
        for (Integer planId : planIds) {
            Optional<Plan> OPlan = planRepository.findById(planId);
            if (!OPlan.isPresent()) {
                throw new NotFoundException("Plan not found.");
            }
            Plan plan = OPlan.get();
            List<String> allAllocations = plan.getAllocationNumbers();
            List<String> finishedAllocations = plan.getFinishedAllocation();
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
    public List<JsonableCompletion> getCompletionCountForPlans(Integer userId) {
        List<Integer> planIds = getPlanIds(userId);
        List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
        Date beginOfLastWeek = periodOfLastWeek.get(0);
        Date endOfLastWeek = periodOfLastWeek.get(1);
        List<JsonableCompletion> jsonableCompletionList = new ArrayList<>();
        for (Integer planId : planIds) {
            int total = 0;
            int finished = 0;
            Optional<Plan> OPlan = planRepository.findById(planId);
            if (!OPlan.isPresent()) {
                throw new NotFoundException("Plan not found.");
            }
            Plan plan = OPlan.get();
            List<String> allAllocations = plan.getAllocationNumbers();
            List<String> finishedAllocations = plan.getFinishedAllocation();
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
            jsonableCompletionList.add(new JsonableCompletionPerPlan(planId, finished, total));
        }
        return jsonableCompletionList;
    }

    @Override
    public Integer getPlanCount(Integer userId) {
        int cnt = 0;
        List<Integer> planIds = getPlanIds(userId);
        for (Integer planId : planIds) {
            Optional<Plan> OPlan = planRepository.findById(planId);
            if (!OPlan.isPresent()) {
                throw new NotFoundException("Plan not found.");
            }
            Plan plan = OPlan.get();
            Date createDate = plan.getCreateDate();
            List<Date> periodOfLastWeek = DaysOfLastWeekGetter.getDaysOfLastWeek();
            Date beginOfLastWeek = periodOfLastWeek.get(0);
            Date endOfLastWeek = periodOfLastWeek.get(1);
            if (createDate.after(beginOfLastWeek) && createDate.before(endOfLastWeek)) {
                cnt++;
            }
        }
        return cnt;
    }


    private boolean allocatePlan(Integer planId) {
        Optional<Plan> OPlan = planRepository.findById(planId);
        if (!OPlan.isPresent()) {
            throw new InnerDataTransmissionException("Plan not exists.");
        }
        Plan plan = OPlan.get();
        int duration = plan.getDuration() - plan.getAllocatedDuration();
        if (duration < 0) {
            throw new InnerDataTransmissionException("Duration setting is not proper.");
        }
        if (duration == 0) {
            plan.setIsCompleted(true);
            return true;
        }
        Appointment appointment = appointmentGenerator.convertToAppointment(plan);
        log.info(appointment.toString());
        String requestNumber = timeService.createAllocTask(plan.getUserId(), appointment);
        if (requestNumber == null) {
            return false;
        }
        log.info(planId + ": " + requestNumber);
        if (planRequest.getPlanRequest().containsKey(planId)) {
            planRequest.getPlanRequest().get(planId).add(requestNumber);
        } else {
            planRequest.getPlanRequest().put(planId, new ArrayList<>(Arrays.asList(requestNumber)));
        }
        return true;
    }

    private int searchStartDateInAllocations(Plan plan, Date date) {
        Integer code = timeBlockCodeGenerator.get(date);
        List<String> allocationNumbers = plan.getAllocationNumbers();
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
