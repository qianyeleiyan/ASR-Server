package org.codesdream.asr.component.json.model;

import lombok.Data;

import lombok.NoArgsConstructor;
import org.codesdream.asr.model.task.Plan;
import org.springframework.stereotype.Component;


@Data
@Component
@NoArgsConstructor
public class JsonablePlan extends JsonableTaskPlanSuper {

    private Integer mutexPeriod = 0;

    public JsonablePlan(Plan plan) {
        super(plan);
        this.mutexPeriod = plan.getMutexPeriod();
        this.setId(plan.getId());

    }

    public Plan parseModel() {
        Plan plan = (Plan) super.parseModel(0);
        plan.setMutexPeriod(this.mutexPeriod);
        plan.setId(this.getId());
        return plan;
    }
}
