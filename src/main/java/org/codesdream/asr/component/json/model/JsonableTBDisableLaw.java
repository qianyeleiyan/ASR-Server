package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class JsonableTBDisableLaw {
    private Integer userId;
    private Integer dayOfWeeks;
    private List<Integer> scales = new ArrayList<>();
}
