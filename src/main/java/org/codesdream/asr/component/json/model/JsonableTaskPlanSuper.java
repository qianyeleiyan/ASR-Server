package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codesdream.asr.component.datamanager.DescriptionGenerator;
import org.codesdream.asr.component.task.TaskPlanSuper;;
import org.codesdream.asr.model.task.Plan;
import org.codesdream.asr.model.task.Task;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Data
@Component
@NoArgsConstructor
public class JsonableTaskPlanSuper {

    private Integer id;

    private List<String> description;

    private Date deadline;

    private Float importantDegree = 1.0f;

    private Integer preference = 0;

    private Integer userId;

    private Integer singleMin = 0;

    private Integer singleMax = 0x7fffffff;

    private Integer duration = 0;

    private Float urgencyPreference = 1.0f;

    private Date createTime = new Date();

    public JsonableTaskPlanSuper(TaskPlanSuper taskPlanSuper) {
        DescriptionGenerator generator = new DescriptionGenerator();
        this.description = generator.getStringList(taskPlanSuper.getDescription());

        this.deadline = taskPlanSuper.getDeadline();
        this.importantDegree = taskPlanSuper.getImportantDegree();
        this.preference = taskPlanSuper.getPreference();
        this.userId = taskPlanSuper.getUserId();

        this.singleMin = taskPlanSuper.getSingleMin();
        this.singleMax = taskPlanSuper.getSingleMax();
        this.duration = taskPlanSuper.getDuration();
        this.createTime = taskPlanSuper.getCreateDate();

    }

    public TaskPlanSuper parseModel(Integer type) {
        TaskPlanSuper taskPlanSuper;
        if (type.intValue() == 1) {
            taskPlanSuper = new Task();
        } else {
            taskPlanSuper = new Plan();
        }
        DescriptionGenerator generator = new DescriptionGenerator();

        taskPlanSuper.setDescription(generator.getString(this.description));
        taskPlanSuper.setImportantDegree(this.importantDegree);
        taskPlanSuper.setPreference(this.preference);
        taskPlanSuper.setDeadline(this.deadline);
        taskPlanSuper.setUserId(this.userId);
        taskPlanSuper.setUrgencyPreference(this.urgencyPreference);

        taskPlanSuper.setSingleMin(this.singleMin);
        taskPlanSuper.setSingleMax(this.singleMax);
        taskPlanSuper.setDuration(this.duration);
        taskPlanSuper.setCreateDate(this.createTime);

        return taskPlanSuper;
    }
}
