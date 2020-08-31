package org.codesdream.asr.component.json.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class JsonableCompletion {

    protected Integer finished;
    protected Integer total;
}
