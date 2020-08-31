package org.codesdream.asr.exception.notfound;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserNotFoundException extends NotFoundException {
    Integer id;
    String username;
    public UserNotFoundException(Integer id, String username){
        super();
        this.id = id;
        this.username = username;
    }

    public UserNotFoundException(String msg){
        super(msg);
    }
}
