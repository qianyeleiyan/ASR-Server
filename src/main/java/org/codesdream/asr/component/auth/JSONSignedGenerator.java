package org.codesdream.asr.component.auth;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 客户端签名生成器
 */
@Component
public class JSONSignedGenerator {
    @Resource
    SHA1Encoder encoder;

    /**
     * 生成签名
     * @param username 用户名
     * @param randomCode 随机特征值
     * @param token Token
     * @return 客户端签名
     */
    public String generateSigned(String username, String randomCode, String token){
        return encoder.encode(String.format("SIGN [%s][%s][%s]",username, randomCode, token));
    }
}
