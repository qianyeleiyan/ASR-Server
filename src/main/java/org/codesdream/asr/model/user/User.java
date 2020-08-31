package org.codesdream.asr.model.user;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    // 用户名(openid)
    @Column(unique = true, nullable = false)
    private String username;

    // 密码（必须以哈希值sha256储存）
    @Column(nullable = false)
    private String password;

    // 账号是否过期
    private boolean accountNonExpired = true;

    // 账号是否被封禁
    private boolean accountNonLocked = true;

    // 证书是否过期
    private boolean credentialsNonExpired = true;

    // 账号是否激活
    private  boolean enabled = true;

    // 是否删除
    @Column(nullable = false)
    private boolean deleted = false;

    // 访问控制角色(不在数据表中) Spring Security
    private transient Collection<?extends GrantedAuthority> authorities;

    // 用户详细信息
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserDetail userDetail = new UserDetail();

    // 用户认证表
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserAuth userAuth = new UserAuth();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserBuffer userBuffer = new UserBuffer();


    public User(String username, String password) {
        this.username = username;
        this.password = password;

        initDefault();
    }

    public User() {
        this.username = null;
        this.password = null;
        this.deleted = false;

        initDefault();
    }

    // 用默认的方式初始化User对象的值
    private void initDefault(){

    }

}
