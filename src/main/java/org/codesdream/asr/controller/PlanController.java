package org.codesdream.asr.controller;

import org.codesdream.asr.component.datamanager.JSONParameter;
import org.codesdream.asr.component.json.model.*;
import org.codesdream.asr.component.task.AccomplishmentMarker;
import org.codesdream.asr.exception.notfound.NotFoundException;
import org.codesdream.asr.exception.notfound.PlansNotFound;
import org.codesdream.asr.model.task.Plan;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.repository.task.PlanRepository;
import org.codesdream.asr.service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/plan")
public class PlanController {
    @Resource
    private PlanRepository planRepository;

    @Resource
    private JSONParameter jsonParameter;

    @Resource
    private PlanService planService;

    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonablePlan createPlan(@RequestBody JsonablePlan jsonablePlan, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        jsonablePlan.setUserId(user.getId());
        return new JsonablePlan(planService.registerPlan(jsonablePlan));
    }

    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.OK)
    public JsonablePlan getPlan(@RequestParam Integer id) {
        Optional<Plan> planOptional = planRepository.findById(id);
        if (!planOptional.isPresent()) throw new NotFoundException(id.toString());
        return new JsonablePlan(planOptional.get());
    }

    @GetMapping("ids")
    @ResponseStatus(HttpStatus.OK)
    public List<Integer> getPlansIds(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return planService.getPlanIds(user.getId());
    }

    @GetMapping("details")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonablePlan> getPlans(@RequestParam(name = "ids") List<Integer> ids) {
        List<JsonablePlan> plans = new ArrayList<>();
        for (Integer planId : ids) {
            Optional<Plan> planOptional = planRepository.findById(planId);
            if (!planOptional.isPresent()) throw new NotFoundException();

            plans.add(new JsonablePlan(planOptional.get()));
        }
        return plans;
    }

    @PostMapping(value = "update")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonablePlan updatePlan(@RequestBody String updaterStr) {
        JsonableUpdater updater = jsonParameter.createJsonableUpdater(updaterStr);
        Optional<Plan> planOptional = planRepository.findById(updater.getId());
        if (!planOptional.isPresent()) throw new NotFoundException(updater.getId().toString());

        JsonablePlan jsonablePlan = new JsonablePlan(planOptional.get());

        jsonablePlan = jsonParameter.parsePathToObject(updater.getPatch(), jsonablePlan);

        return planService.update(jsonablePlan);
    }

    @PostMapping("delete")
    @ResponseStatus(HttpStatus.OK)
    public JsonableDeletion deletePlan(@RequestBody Map<String, Object> planIdMap, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Integer> planIds = (List<Integer>) planIdMap.get("id");
        List<Integer> successfulDeletion = new ArrayList<>();
        List<Integer> failedDeletion = new ArrayList<>();
        boolean flag;
        for (Integer planId : planIds) {
            if (planService.deletePlan(planId, user.getId())) {
                successfulDeletion.add(planId);
            } else {
                failedDeletion.add(planId);
            }
        }
        if (successfulDeletion.size() == planIds.size()) flag = true;
        else flag = false;
        return new JsonableDeletion(successfulDeletion, failedDeletion, flag);
    }

    @GetMapping("results")
    @ResponseStatus(HttpStatus.OK)
    public JsonableResult getResults(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return planService.getAllResults(user.getId());
    }

    @GetMapping("today")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonablePlanResult> getPlanForToday(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        JsonableResult jsonableResult = planService.getAllResults(user.getId());
        if (jsonableResult == null) {
            throw new PlansNotFound("No plan registered for current user.");
        }
        List<JsonablePlanResult> jsonablePlanResults = jsonableResult.getPlanResultList();
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = format.format(today);
        List<JsonablePlanResult> planResultsForToday = new ArrayList<>();
        for (JsonablePlanResult jsonablePlanResult : jsonablePlanResults) {
            String beginString = format.format(jsonablePlanResult.getBegin());
            if (today.before(jsonablePlanResult.getEnd())) {
                if (beginString.equals(todayString)) {
                    planResultsForToday.add(jsonablePlanResult);
                } else {
                    break;
                }
            }
        }
        return planResultsForToday;
    }

    @PostMapping("mark")
    @ResponseStatus(HttpStatus.OK)
    public boolean markPlanFinished(@RequestBody AccomplishmentMarker accomplishmentMarker) {
        Integer planId = accomplishmentMarker.getId();
        Date start = accomplishmentMarker.getDate();
        return planService.markFinished(planId, start);
    }

    @GetMapping("completion/total")
    @ResponseStatus(HttpStatus.OK)
    public Map<Integer, Integer> getTotalCompletionCount(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return planService.getTotalCompletionCount(user.getId());
    }

    @GetMapping("completion/per_day")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonableCompletion> getCompletionPerDayForLastWeek(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return planService.getCompletionCountForDays(user.getId());
    }

    @GetMapping("completion/per_plan")
    @ResponseStatus(HttpStatus.OK)
    public List<JsonableCompletion> getComoletoinPerPlanForLastWeek(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return planService.getCompletionCountForPlans(user.getId());
    }

    @GetMapping("creation_count/last_week")
    @ResponseStatus(HttpStatus.OK)
    public Integer getCountOfPlanCreationForLastWeek(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return planService.getPlanCount(user.getId());
    }
}
