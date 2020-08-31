package org.codesdream.asr.component.json.request;

import lombok.Data;

/**
 * 用户登录请求对象
 */
@Data
public class UserLoginChecker {

    // 请求类型
    private String checkType;

    // openid
    private String openid;

    // 密码
    private String password;

    // 客户端代码
    private String clientCode;
}
