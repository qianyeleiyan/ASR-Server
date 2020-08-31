package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonablePeriod {
    Integer startScale;

    Integer endScale;

    public JsonablePeriod(Integer startScale, Integer endScale){
        this.startScale = startScale;
        this.endScale = endScale;
    }
}
