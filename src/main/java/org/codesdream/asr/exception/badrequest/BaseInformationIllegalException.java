package org.codesdream.asr.exception.badrequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseInformationIllegalException extends IllegalException {
    String type;
    String value;

    public BaseInformationIllegalException(Class<?> aClass, String value){
        super();
        this.type = aClass.getName();
        this.value = value;
    }
}
