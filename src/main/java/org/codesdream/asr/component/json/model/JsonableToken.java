package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JsonableToken {
    private String openid;

    private String token;

    public JsonableToken(String openid, String token){
        this.openid = openid;
        this.token = token;
    }
}
