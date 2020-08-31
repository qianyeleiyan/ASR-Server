package org.codesdream.asr.exception.innerservererror;

import lombok.NoArgsConstructor;

/**
 * 内部接口数据传递异常
 */
@NoArgsConstructor
public class InnerDataTransmissionException extends HandlingErrorsException {
    public InnerDataTransmissionException(String msg){
        super(msg);
    }
}
