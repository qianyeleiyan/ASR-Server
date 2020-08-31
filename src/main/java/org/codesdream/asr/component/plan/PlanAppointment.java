package org.codesdream.asr.component.plan;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PlanAppointment {

    private Date deadLine;

    private Float importantDegree = 0.0f;

    private Integer preference = 0;

    private Integer singleMin = 0;

    private Integer singleMax = 0;

    private Integer duration = 0;

    private Integer mutexPeriod = 0;
}