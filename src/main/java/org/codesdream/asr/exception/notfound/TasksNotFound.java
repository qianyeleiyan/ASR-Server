package org.codesdream.asr.exception.notfound;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TasksNotFound extends NotFoundException {
    public TasksNotFound(String msg) {
        super(msg);
    }

    public TasksNotFound() {
        super();
    }
}
