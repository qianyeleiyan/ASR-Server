package org.codesdream.asr.component.task;

import lombok.Data;
import org.hibernate.mapping.Set;

import javax.persistence.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@MappedSuperclass
public class TaskPlanSuper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    protected Integer userId;

    protected String description;

    protected Date deadline;

    protected Date createDate = new Date();

    protected Float importantDegree = 0.0f;

    protected Float urgencyPreference = 1.0f;

    protected Integer preference = 0;

    protected Integer singleMin = 0;

    protected Integer singleMax = 0xfffffff;

    protected Integer duration = 0;

    protected Integer mutexPeriod = 0;

    protected Integer allocatedDuration = 0;

    protected Integer finishedDuration = 0;

    protected Boolean isCompleted = false;

    protected AtomicInteger markCount = new AtomicInteger(0);

    @ElementCollection
    private List<String> allocationNumbers = new LinkedList<>();

    @ElementCollection
    protected List<String> finishedAllocation = new LinkedList<>();

    @ElementCollection
    protected List<String> failedAllocation = new LinkedList<>();
}
