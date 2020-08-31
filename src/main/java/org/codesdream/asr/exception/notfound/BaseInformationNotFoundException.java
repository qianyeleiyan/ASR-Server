package org.codesdream.asr.exception.notfound;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseInformationNotFoundException extends NotFoundException {
    private String className;
    private String value;

    public BaseInformationNotFoundException(Class<?> baseInformationClass, String value){
        super(String.format("%s: %s", baseInformationClass.getName(), value));
        this.className = baseInformationClass.getName();
        this.value = value;
    }
}
