package org.codesdream.asr.component.time;

import javafx.util.Pair;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Calendar;

@Component
public class TimeScaleGenerator implements Serializable {

    public int get(int hour, int minute){
        return hour * 2 + (int) Math.floor(minute / 30.0);
    }

    public int getCurrent(){
        return get(Calendar.getInstance());
    }

    public int get(Calendar calendar){
        return get(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }

    public int getScale(int code){
        return code - (code / 100);
    }

    public Pair<Integer, Integer> getTime(int scale){
        int hour, minute;
        if(scale % 2 == 0) minute = 0;
        else minute = 30;

        hour = scale / 2;

        return new Pair<>(hour, minute);
    }

}
