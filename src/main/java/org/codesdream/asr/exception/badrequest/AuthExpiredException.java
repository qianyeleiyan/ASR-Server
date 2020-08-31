package org.codesdream.asr.exception.badrequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 认证信息过期
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AuthExpiredException extends IllegalException {
    public AuthExpiredException(String msg){
        super(msg);
    }
}
