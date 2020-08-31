package org.codesdream.asr.model.auth;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

// token记录
@Data
@Entity
@Table(name = "json_tokens")
public class JSONToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // token对应的用户
    @Column(unique = true)
    private String username;

    // token值
    @Column(unique = true)
    private String token;

    // 客户端标识口令
    private String sessionKey;

    // token过期时间
    private Date expiredDate;
}
