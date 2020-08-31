package org.codesdream.asr.exception.badrequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseInformationAlreadyExistException extends AlreadyExistException {
    private String className;
    private String value;

    public BaseInformationAlreadyExistException(Class<?> aClass, String value){
        super(String.format("%s: %s", aClass.getName(), value));
        this.className = aClass.getName();
        this.value = value;
    }
}
