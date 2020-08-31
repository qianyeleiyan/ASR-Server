package org.codesdream.asr.component.json.model;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonableUpdater {
    private Integer id;
    private JsonPatch patch;
}
