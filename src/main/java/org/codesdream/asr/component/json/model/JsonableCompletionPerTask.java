package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JsonableCompletionPerTask extends JsonableCompletion {

    private Integer taskId;

    public JsonableCompletionPerTask(Integer taskId, Integer finished, Integer total) {
        super(finished, total);
        this.taskId = taskId;
    }
}
