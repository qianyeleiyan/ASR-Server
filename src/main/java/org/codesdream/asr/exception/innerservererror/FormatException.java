package org.codesdream.asr.exception.innerservererror;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FormatException extends RuntimeException {
    public FormatException(String msg){
        super(msg);
    }
}
