package org.codesdream.asr.component.time;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.task.Appointment;

import javax.annotation.Resource;

@Data
@Slf4j
public class TimeRegionsManager {

    @Resource
    private TimeBlockCodeGenerator codeGenerator;

    // 时间分配处理器
    private TATaskProcessor taskProcessor;

    // 时间管理处理器群
    private TMProcessor tmProcessor;

    public TimeRegionsManager(TATaskProcessor taskProcessor, TMProcessor tmProcessor){
        this.taskProcessor = taskProcessor;
        this.tmProcessor = tmProcessor;
    }

    // 分配回滚
    public boolean rollback(String allocId){
        return tmProcessor.rollback(allocId);
    }

    // 时间分配
    synchronized public String allocTime(Appointment appointment) {

        log.info("Start Processing Task Appointment...");

        TimeAppointTask timeAppointTask = new TimeAppointTask();

        if(appointment.getDuration() <= 0) return null;
        timeAppointTask.duration = appointment.getDuration();

        if(appointment.getSingleMin() <= 0) return null;
        timeAppointTask.singleMin = appointment.getSingleMin();

        if(appointment.getSingleMax() <= 0 || appointment.getSingleMax() < appointment.getSingleMin()) return null;
        timeAppointTask.singleMax = appointment.getSingleMax();

        timeAppointTask.endCode = codeGenerator.get(appointment.getDeadline());

        if(appointment.getMode() < 0) return null;
        timeAppointTask.mode = appointment.getMode();

        if(appointment.getImportantDegree() <= 0) return null;
        timeAppointTask.importance = appointment.getImportantDegree();

        if(appointment.getPreference() < -1 || appointment.getPreference() > 2) return null;
        timeAppointTask.preferenceTime = appointment.getPreference();

        if(appointment.getUrgencyPreference() <= 0) return null;
        timeAppointTask.earlyLate = appointment.getUrgencyPreference();

        if(appointment.getType() == null) return null;
        timeAppointTask.type = appointment.getType();

        if(appointment.getType() == 1 && appointment.getMutexPeriod() == null) return null;
        timeAppointTask.mutexPeriod = appointment.getMutexPeriod();

        log.info(String.format("Last Checking Task Appoint Task: %s.", timeAppointTask.uuid));

        // 最后的检查
        if(timeAppointTask.earlyLate == null
                || timeAppointTask.duration == null
                || timeAppointTask.singleMin == null
                || timeAppointTask.singleMax == null
                || timeAppointTask.endCode == null
                || timeAppointTask.mode == null
                || timeAppointTask.importance == null
                || timeAppointTask.preferenceTime == null
                || timeAppointTask.type == null) return null;

        log.info(String.format("Start Register Task Appoint Task: %s ...", timeAppointTask.uuid));
        taskProcessor.registerTimeAppointTask(timeAppointTask);
        log.info(String.format("Task Appoint Task Register Done: %s.", timeAppointTask.uuid));
        return timeAppointTask.uuid;
    }

}
