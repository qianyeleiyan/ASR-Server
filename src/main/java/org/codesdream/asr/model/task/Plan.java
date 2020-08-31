package org.codesdream.asr.model.task;

import lombok.Data;
import org.codesdream.asr.component.task.TaskPlanSuper;
import org.codesdream.asr.model.time.TimeBlock;
import org.hibernate.mapping.Set;

import javax.naming.Name;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Table(name = "plan")
public class Plan extends TaskPlanSuper {

}
