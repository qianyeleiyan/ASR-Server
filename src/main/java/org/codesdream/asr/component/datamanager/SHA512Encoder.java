package org.codesdream.asr.component.datamanager;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

/**
 * SHA256编码器
 */
@Component
public class SHA512Encoder {
    /**
     * 编码
     * @param charSequence 字符队列
     * @return 密文
     */
    public String encode(CharSequence charSequence){
        return DigestUtils.sha512Hex(charSequence.toString());
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
