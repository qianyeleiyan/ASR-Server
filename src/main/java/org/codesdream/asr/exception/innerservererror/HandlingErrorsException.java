package org.codesdream.asr.exception.innerservererror;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

// 处理错误对应的异常类
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class HandlingErrorsException extends RuntimeException {
    public HandlingErrorsException(String msg){
        super(msg);
    }
}
