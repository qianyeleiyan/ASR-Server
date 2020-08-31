package org.codesdream.asr.component.auth;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码编码器
 */
@Component
public class ASRPasswordEncoder implements PasswordEncoder {
    /**
     * 密码编码
     * @param charSequence
     * @return 密文
     */
    @Override
    public String encode(CharSequence charSequence) {
        return DigestUtils.sha256Hex(charSequence.toString());
    }

    /**
     * 密码验证
     * @param charSequence 字符队列
     * @param s 密文
     * @return 布尔值
     */
    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return s.equals(DigestUtils.sha256Hex(charSequence.toString()));
    }
}
