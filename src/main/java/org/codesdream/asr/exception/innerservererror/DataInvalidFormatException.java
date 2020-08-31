package org.codesdream.asr.exception.innerservererror;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataInvalidFormatException extends FormatException {
    String information;

    public DataInvalidFormatException(Exception e){
        super();
        information = e.getMessage();
    }

    public DataInvalidFormatException(){
        super();
    }
}
