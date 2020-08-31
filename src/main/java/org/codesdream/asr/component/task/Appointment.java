package org.codesdream.asr.component.task;

import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@ToString
@Component
public class Appointment {

    private Integer duration;

    private Float importantDegree = 0.0f;

    private Integer preference = 0;

    private Integer singleMin = 0;

    private Integer singleMax = 0;

    private Integer userId;

    private Date deadline;

    private Float urgencyPreference;

    private Integer mode = 1;

    private Integer mutexPeriod = 1;

    private Integer type;
}
