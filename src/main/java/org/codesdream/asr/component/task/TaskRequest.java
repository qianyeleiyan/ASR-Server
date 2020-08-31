package org.codesdream.asr.component.task;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class TaskRequest {

    private static TaskRequest instance = new TaskRequest();

    private TaskRequest() {
        this.taskRequest = new HashMap<>();
    }

    public static TaskRequest getInstance() {
        return instance;
    }

    private Map<Integer, List<String>> taskRequest;
}
