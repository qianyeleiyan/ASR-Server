package org.codesdream.asr.model.task;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table
public class AllocationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;


}
