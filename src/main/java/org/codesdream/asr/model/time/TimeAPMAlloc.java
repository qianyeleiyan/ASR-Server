package org.codesdream.asr.model.time;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "time_apm_alloc")
public class TimeAPMAlloc implements Comparable<TimeAPMAlloc> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String apmId;

    private Float weight;

    private Integer duration;

    private Integer startCode;

    private Boolean rollback = false;

    private String requestId;

    @ElementCollection
    private List<Integer> codeList = new ArrayList<>();

    @Override
    public int compareTo(TimeAPMAlloc o) {
        return startCode - o.getStartCode();
    }
}
