package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codesdream.asr.model.task.Task;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor
public class JsonableTask extends JsonableTaskPlanSuper {

    private Float urgencyPreference;

    public JsonableTask(Task task) {
        super(task);
        this.setUrgencyPreference(task.getUrgencyPreference());
        this.setId(task.getId());
    }

    public Task parseModel() {
        Task task = (Task) super.parseModel(1);
        task.setId(this.getId());
        return task;
    }
}