package org.codesdream.asr.service;

import org.codesdream.asr.model.auth.JSONToken;

import java.util.Optional;

/**
 * 身份验证服务接口
 */
public interface IAuthService {

    /**
     * 通过用户名查找与对应用户相关联的token
     * @param username 用户名（openid）
     * @return Token对象
     */
    Optional<JSONToken> findTokenByUserName(String username);

    /**
     * 检查token是否过期
     * @param token Token对象
     * @return 布尔值
     */
    boolean checkTokenIfExpired(JSONToken token);

    /**
     * 为用户获得一个新的API Token
     * @param username 用户名（openid）
     * @param clientCode 客户端代码
     * @return Token值
     */
    Optional<String> userNewTokenGetter(String username, String clientCode);

    /**
     * 获得一个新的预验证码
     * @return 预验证码
     */
    String preValidationCodeGetter();

    /**
     * 检验预验证码是否有效
     * @param pvc 预验证码
     * @return 布尔值
     */
    boolean preValidationCodeChecker(String pvc);
}
