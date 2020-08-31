package org.codesdream.asr.service;

import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.model.time.TimeAPMAlloc;

import java.util.List;

public interface ITimeService {

    String createAllocTask(Integer userId, Appointment appointment);

    boolean rollbackAllocTask(Integer userId, String allocId);

    List<TimeAPMAlloc> getAllocTaskResult(Integer userId, String allocId);

    void updateTimeBlockDisabledLaw(Integer userId);

}
