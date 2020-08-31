package org.codesdream.asr.model.user;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_buffer")
public class UserBuffer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer TBPBufferFileId = null;

    private Integer TRPBufferFileId = null;

    private Integer TAPBufferFileId = null;

    private boolean active = false;
}
