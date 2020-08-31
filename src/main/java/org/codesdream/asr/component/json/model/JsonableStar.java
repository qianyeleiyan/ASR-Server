package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codesdream.asr.model.user.User;

@Data
@NoArgsConstructor
public class JsonableStar {

    private Integer starNum;

    private String description;

    public JsonableStar(User user){
        this.starNum = user.getUserDetail().getStar();
        if(this.starNum == null){
            this.starNum = 3;
        }

        if(this.starNum == 5) this.description = "优秀的执行力";
        else if(this.starNum == 4) this.description = "良好的执行力";
        else if(this.starNum == 3) this.description = "执行力尚可提高";
        else this.description = "执行力较差";
    }
}
