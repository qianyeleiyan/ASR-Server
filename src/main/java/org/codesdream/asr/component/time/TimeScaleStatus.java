package org.codesdream.asr.component.time;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TimeScaleStatus {

    public static final int MORNING = 0;

    public static final int AFTERNOON = 1;

    public static final int NIGHT = 2;

    @Resource
    private TimeScaleGenerator scaleGenerator;

    public int getCodeScaleStatus(int code){
        int scale = scaleGenerator.getScale(code % 100);
        if(scale >= 12 && scale < 24) return MORNING;
        else if(scale >= 24 && scale < 36) return AFTERNOON;
        else return NIGHT;
    }
}
