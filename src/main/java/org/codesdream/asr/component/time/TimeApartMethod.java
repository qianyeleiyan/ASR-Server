package org.codesdream.asr.component.time;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class TimeApartMethod implements Serializable {

    private boolean allocated = false;

    private Float fittedRate = 0.0f;

    private List<Integer> durations = new ArrayList<>();

}
