package org.codesdream.asr.component.json.respond;

import lombok.Data;

/**
 * 用户登录请求应答
 */
@Data
public class UserLoginCheckerJSONRespond {

    // 用户是否存在
    Boolean userExist = null;

    // 用户是否被封禁
    Boolean userBanned = null;

    // 登录状态
    Boolean loginStatus = null;

    // 返回附加信息
    String respondInformation = null;

    // Token
    String token = null;

    // 用户ID号（数据库）
    String uid = null;

    // 预验证码
    String pvc = null;

}
