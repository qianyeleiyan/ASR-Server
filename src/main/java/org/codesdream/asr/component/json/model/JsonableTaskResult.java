package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class JsonableTaskResult implements Comparable<JsonableTaskResult> {

    private Integer taskId;

    private Date begin;

    private Date end;

    // 0: future; 1: finished; 2: failed;
    private Integer status;

    @Override
    public int compareTo(JsonableTaskResult o) {
        if (begin.before(o.getBegin())) {
            return -1;
        } else if (begin.after(o.getBegin())) {
            return 1;
        } else return 0;
    }
}
