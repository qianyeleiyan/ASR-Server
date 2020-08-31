package org.codesdream.asr.model.record;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class TATRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer userId;

    private boolean finished;

    private String requestId;

}
