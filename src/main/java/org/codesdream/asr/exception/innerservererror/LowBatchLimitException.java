package org.codesdream.asr.exception.innerservererror;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LowBatchLimitException extends RuntimeException {

    private String message = "Low batch limit, please set a higher one.";

    public LowBatchLimitException() {
        super();
    }

    public LowBatchLimitException(String message) {
        this.message = message;
    }
}
