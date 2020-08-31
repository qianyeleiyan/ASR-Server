package org.codesdream.asr.component.json.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class JsonableResult {
    List<JsonableTaskResult> taskResultList = new ArrayList<>();
    List<JsonablePlanResult> planResultList = new ArrayList<>();
    List<Integer> failedIds = new ArrayList<>();

    public JsonableResult(List<JsonableTaskResult> taskResultList, List<JsonablePlanResult> planResultList,
                          List<Integer> failedIds) {
        this.taskResultList = taskResultList;
        this.planResultList = planResultList;
        this.failedIds = failedIds;
    }

    public JsonableResult() {
    }
}
