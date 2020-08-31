package org.codesdream.asr.exception.badrequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AlreadyExistException extends RuntimeException {
    public AlreadyExistException(String msg){
        super(msg);
    }
}
