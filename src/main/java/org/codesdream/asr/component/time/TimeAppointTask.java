package org.codesdream.asr.component.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeAppointTask implements Serializable {

    String uuid = UUID.randomUUID().toString();

    Integer endCode;

    Integer duration;

    Integer preferenceTime;

    Float importance;

    Float earlyLate;

    Integer singleMin, singleMax;

    Integer mode;

    Integer mutexPeriod;

    Integer type;

    List<TimeApartMethod> timeDurationMethods = new ArrayList<>();

    // 最终分配方案
    List<TimeAPMPlan> apmPlans = new ArrayList<>();

    // 分配状态标志位
    boolean success = false;

    // 部分分配成功标志位
    boolean half_success = false;


    public void addTimeApartMethod(List<Integer> durations){
        TimeApartMethod timeApartMethod = new TimeApartMethod();
        timeApartMethod.setDurations(durations);
        timeDurationMethods.add(timeApartMethod);
    }

}
