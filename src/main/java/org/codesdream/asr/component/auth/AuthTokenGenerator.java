package org.codesdream.asr.component.auth;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
 * Token生成器
 */
@Component
public class AuthTokenGenerator {
    @Resource
    private SHA1Encoder encoder;

    /**
     * 生成Token
     * @param username 用户名
     * @return token字符串
     */
    public String generateAuthToken(String username){
        Date dateNow = new Date();
        UUID uuid = UUID.randomUUID();
        return encoder.encode(String.format("Token [%s][%d][%s]",username,dateNow.getTime(), uuid.toString()));
    }
}
