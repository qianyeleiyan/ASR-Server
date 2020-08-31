package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class JsonableCompletionPerDay extends JsonableCompletion {

    Integer day;

    public JsonableCompletionPerDay(Integer day, Integer finished, Integer total) {
        super(finished, total);
        this.day = day;
    }
}
