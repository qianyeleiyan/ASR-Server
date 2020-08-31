package org.codesdream.asr.component.task;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PlanRequest {
    private static PlanRequest instance = new PlanRequest();

    private PlanRequest() {
        this.planRequest = new HashMap<>();
    }

    public static PlanRequest getInstance() {
        return instance;
    }

    private Map<Integer, List<String>> planRequest;
}
