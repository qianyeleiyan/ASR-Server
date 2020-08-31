package org.codesdream.asr;

import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.component.time.TimeScaleStatus;
import org.codesdream.asr.component.time.TimeSystemEngine;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.service.ITimeService;
import org.codesdream.asr.service.IUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Calendar;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TimeSystemTest {

    @Resource
    private IUserService userService;

    @Resource
    private ITimeService timeService;

    @Resource
    private TimeSystemEngine timeSystemEngine;

    private User user;


    public TimeSystemTest(){

    }

    @Test
    public void basicTest() {
        this.user = userService.newTestUser("oFy6M5Rao0qQkVosVv1Qeh01Gw-Y");
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
        appointment.setUserId(this.user.getId());

        timeService.createAllocTask(this.user.getId(), appointment);

        timeSystemEngine.process();

    }
}
