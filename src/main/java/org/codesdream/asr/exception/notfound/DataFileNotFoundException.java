package org.codesdream.asr.exception.notfound;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataFileNotFoundException extends NotFoundException {
    private String path;

    public DataFileNotFoundException(String msg){
        super(msg);
        this.path = msg;
    }
}
