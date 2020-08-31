package org.codesdream.asr.model.user;

import lombok.Data;
import org.codesdream.asr.model.task.PlanPool;
import org.codesdream.asr.model.task.TaskPool;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户相关详细信息
 */
@Data
@Entity
@Table(name = "user_detail")
public class UserDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String nickName = null;

    private Integer gender = null;

    private String language = null;

    private String city = null;

    private String province = null;

    private String country = null;

    private String avatarUrl = null;

    private String phoneNumber = null;

    private String uniqueId = null;

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> complain = new ArrayList<>();

    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> advice = new ArrayList<>();

    @OneToOne
    private TaskPool taskPool;

    @OneToOne
    private PlanPool planPool;

    private Integer star = 3;
}
