package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
public class JsonableDeletion {
    List<Integer> successfulDeletion;
    List<Integer> failedDeletion;
    boolean flag;

    public JsonableDeletion(List<Integer> successfulDeletion, List<Integer> failedDeletion, boolean flag) {
        this.successfulDeletion = successfulDeletion;
        this.failedDeletion = failedDeletion;
        this.flag = flag;
    }
}
