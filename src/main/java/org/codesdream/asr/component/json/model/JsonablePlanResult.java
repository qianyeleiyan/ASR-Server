package org.codesdream.asr.component.json.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Data
@Component
public class JsonablePlanResult implements Comparable<JsonablePlanResult> {

    private Integer taskId;

    private Date begin;

    private Date end;

    private Integer status;

    @Override
    public int compareTo(JsonablePlanResult o) {
        if (begin.before(o.getBegin())) {
            return -1;
        } else if (begin.after(o.getBegin())) {
            return 1;
        } else return 0;
    }
}
