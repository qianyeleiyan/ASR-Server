package org.codesdream.asr.component.auth;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 随机特征值生成器
 */
@Component
public class JSONRandomCodeGenerator {

    @Resource
    private SHA1Encoder encoder;

    /**
     * 生成随机特征值
     * @param username 用户名
     * @param date 时间
     * @param clientCode 客户端代码
     * @return 随机特征值字符串
     */
    public String generateRandomCode(String username, Date date, String clientCode){
        return encoder.encode(String.format("RandomCode [%s][%s][%s]",
                username, Long.toString(date.getTime()), clientCode));
    }
}
