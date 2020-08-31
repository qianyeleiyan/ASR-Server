package org.codesdream.asr.component.auth;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * SHA1编码器
 */
@Component
public class SHA1Encoder {
    /**
     * 编码
     * @param charSequence 字符队列
     * @return 密文
     */
    public String encode(CharSequence charSequence){
        return DigestUtils.sha1Hex(charSequence.toString());
    }

    /**
     * 验证
     * @param charSequence 字符队列
     * @param s 密文
     * @return 布尔值
     */
    public boolean match (CharSequence charSequence, String s){
        return s.equals(encode(charSequence));
    }
}
