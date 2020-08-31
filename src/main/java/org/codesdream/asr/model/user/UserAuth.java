package org.codesdream.asr.model.user;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user_auth")
public class UserAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // 用户身份(普通用户、子服务器)
    private String role = "User";

    private boolean infoAuth = false;

}
