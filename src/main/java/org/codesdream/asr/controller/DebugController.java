package org.codesdream.asr.controller;

import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.component.time.TCGObjectPool;
import org.codesdream.asr.component.time.TimeCalculateGroup;
import org.codesdream.asr.component.time.TimeScaleStatus;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.service.ITimeService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Calendar;

@RestController
@RequestMapping("debug")
public class DebugController {
    @Resource
    private ITimeService timeService;

    @Resource
    private TCGObjectPool objectPool;

    @GetMapping("")
    void getDebug(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Appointment appointment = new Appointment();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +2);

        appointment.setDuration(5);
        appointment.setSingleMin(1);
        appointment.setSingleMax(2);
        appointment.setImportantDegree(10.0f);
        appointment.setDeadline(calendar.getTime());
        appointment.setPreference(TimeScaleStatus.MORNING);
        appointment.setMode(0);
        appointment.setUrgencyPreference(8.0f);
        appointment.setUserId(user.getId());
        appointment.setType(0);

        timeService.createAllocTask(user.getId(), appointment);
    }

    @GetMapping("plan")
    void getDebugPlan(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Appointment appointment = new Appointment();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +180);

        appointment.setDuration(24 * 2);
        appointment.setSingleMin(2);
        appointment.setSingleMax(2);
        appointment.setMutexPeriod(48);
        appointment.setImportantDegree(10.0f);
        appointment.setDeadline(calendar.getTime());
        appointment.setPreference(TimeScaleStatus.MORNING);
        appointment.setMode(0);
        appointment.setType(1);
        appointment.setUrgencyPreference(8.0f);
        appointment.setUserId(user.getId());

        timeService.createAllocTask(user.getId(), appointment);
    }

    @GetMapping("rollback")
    void rollbackTest(Authentication authentication, @RequestParam(name="allocId") String allocId){
        User user = (User) authentication.getPrincipal();
        timeService.rollbackAllocTask(user.getId(), allocId);
    }

    @GetMapping("destroy")
    void destroyTest(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        try {
            TimeCalculateGroup group = objectPool.borrowObject(user.getId());
            group.buffered();
            objectPool.returnObject(user.getId(), group);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
