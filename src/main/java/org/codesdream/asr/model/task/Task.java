package org.codesdream.asr.model.task;

import lombok.Data;
import org.codesdream.asr.component.task.TaskPlanSuper;
import org.codesdream.asr.model.time.TimeBlock;
import org.codesdream.asr.model.user.User;

import javax.persistence.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Table(name = "task")
public class Task extends TaskPlanSuper {

}
