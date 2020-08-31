package org.codesdream.asr.exception.innerservererror;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvalidFormFormatException extends FormatException {

    private String message = "Invalid form format";

    public InvalidFormFormatException(){
        super();
    }

    public InvalidFormFormatException(String message){
        this.message = message;
    }
}
