package org.codesdream.asr.exception.badrequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserInformationIllegalException extends IllegalException {

    String username;

    public UserInformationIllegalException(String username){
        super();
        this.username = username;
    }

}
