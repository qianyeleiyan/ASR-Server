package org.codesdream.asr.model.auth;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "pre_validation_code")
public class PreValidationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String value;

    private Date date = new Date();

}
