package org.codesdream.asr.model.time;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "time_disable_law")
public class TimeDisableLaw {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer dayOfWeek;

    @ElementCollection
    private List<Integer> scale = new ArrayList<>();

    private Integer userId;


}
