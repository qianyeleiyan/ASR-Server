package org.codesdream.asr.component.datamanager;

import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.component.task.TaskPlanSuper;
import org.codesdream.asr.model.task.Task;
import org.springframework.stereotype.Component;

@Component
public class AppointmentGenerator {

    public Appointment convertToAppointment(TaskPlanSuper taskPlanSuper) {
        Integer duration = taskPlanSuper.getDuration() - taskPlanSuper.getAllocatedDuration();
        Appointment appointment = new Appointment();
        appointment.setDuration(duration);
        appointment.setImportantDegree(taskPlanSuper.getImportantDegree());
        appointment.setPreference(taskPlanSuper.getPreference());
        appointment.setSingleMin(taskPlanSuper.getSingleMin());
        appointment.setSingleMax(taskPlanSuper.getSingleMax());
        appointment.setDeadline(taskPlanSuper.getDeadline());
        if (taskPlanSuper.getMutexPeriod() == null || taskPlanSuper.getMutexPeriod() <= 0) {
            taskPlanSuper.setMutexPeriod(1);
        }
        appointment.setMutexPeriod(taskPlanSuper.getMutexPeriod());
        appointment.setMode(0);
        appointment.setUserId(taskPlanSuper.getUserId());
        appointment.setUrgencyPreference(taskPlanSuper.getUrgencyPreference());
        if (taskPlanSuper instanceof Task) {
            appointment.setType(0);
        } else {
            appointment.setType(1);
        }
        System.out.println(appointment.toString());
        return appointment;
    }
}
