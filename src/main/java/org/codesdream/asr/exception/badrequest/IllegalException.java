package org.codesdream.asr.exception.badrequest;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class IllegalException extends RuntimeException {
    public IllegalException(String msg){
        super(msg);
    }
}
