package org.codesdream.asr.model.task;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "plan_pool")
public class PlanPool {

    @Id
    @GeneratedValue
    private Integer id;

    private Integer userId;

    @ElementCollection
    private List<Integer> partlyAppointed = new ArrayList<>();

    @ElementCollection
    private List<Integer> totallyAppointed = new ArrayList<>();

    @ElementCollection
    private List<Integer> unableToAppoint = new ArrayList<>();

    public PlanPool(Integer userId) {
        this.userId = userId;
    }
}
