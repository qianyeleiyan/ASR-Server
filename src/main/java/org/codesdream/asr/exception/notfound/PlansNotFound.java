package org.codesdream.asr.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PlansNotFound extends NotFoundException {
    public PlansNotFound(String msg) {
        super(msg);
    }

    public PlansNotFound() {
        super();
    }
}
