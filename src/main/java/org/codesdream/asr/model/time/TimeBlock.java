package org.codesdream.asr.model.time;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

import javax.persistence.*;


@Data
@Entity
@Table(name = "time_block")
public class TimeBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer code = null;

    private Integer userId = null;

    private String APMId = null;

    private Boolean enable = true;

    private  Boolean used = false;

    private Boolean emergency = false;

}
