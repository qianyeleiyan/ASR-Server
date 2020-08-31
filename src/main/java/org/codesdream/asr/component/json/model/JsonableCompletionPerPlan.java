package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JsonableCompletionPerPlan extends JsonableCompletion {

    private Integer planId;

    public JsonableCompletionPerPlan(Integer planId, Integer finished, Integer total) {
        super(finished, total);
        this.planId = planId;
    }
}
