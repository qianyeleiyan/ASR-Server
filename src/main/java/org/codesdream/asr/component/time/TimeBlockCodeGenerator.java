package org.codesdream.asr.component.time;

import io.swagger.models.auth.In;
import javafx.util.Pair;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.time.Period;
import java.util.*;

@Component
public class TimeBlockCodeGenerator implements Serializable {

    private static final int[] days = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private static final int[] months = {
            Calendar.JANUARY,
            Calendar.FEBRUARY,
            Calendar.MARCH,
            Calendar.APRIL,
            Calendar.MAY,
            Calendar.JUNE,
            Calendar.JULY,
            Calendar.AUGUST,
            Calendar.SEPTEMBER,
            Calendar.OCTOBER,
            Calendar.NOVEMBER,
            Calendar.DECEMBER
    };

    private static final TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

    @Resource
    private TimeScaleGenerator scaleGenerator;

    public Integer get(Calendar date){
        return date.get(Calendar.YEAR) * 1000000
                + (date.get(Calendar.MONTH) + 1) * 10000
                + date.get(Calendar.DAY_OF_MONTH) * 100
                + scaleGenerator.get(date);
    }

    public Integer get(Date date){
        return get(toCalendar(date));
    }

    public Integer getCurrent(){
        Calendar calendar = Calendar.getInstance(timeZone);
        return get(calendar);
    }

    public static Calendar toCalendar(Date date){
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(date);
        return cal;
    }

    public Date toDate(Integer code){
        return toCalendar(code).getTime();
    }

    public Calendar toCalendar(int code){
        int year = code / 1000000;
        int month = code / 10000 % 100;
        int day = code / 100 % 100;
        int scale = code % 100;

        Pair<Integer, Integer> time = scaleGenerator.getTime(scale);

        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(year, months[month - 1], day, time.getKey(), time.getValue());
        return calendar;
    }

    public Calendar getInstanceCalendar(){
        return Calendar.getInstance(timeZone);
    }

    public Calendar getCalendarFromCode(Integer code){
        return toCalendar(code);
    }

    public int getNext(int code){
        Calendar calendar = toCalendar(code);
        calendar.add(Calendar.MINUTE, +30);
        return get(calendar);
    }

    public int getNext(Calendar calendar){
        calendar.add(Calendar.MINUTE, +30);
        return get(calendar);
    }

    public int getNextFromDuration(Calendar calendar, Integer duration){
        calendar.add(Calendar.MINUTE, +30 * (duration - 1));
        return get(calendar);
    }

    public int getBefore(int code){
        Calendar calendar = toCalendar(code);
        calendar.add(Calendar.MINUTE, -30);

        return get(calendar);
    }

    int getDuration(int startCode, int endCode){
        Date startDate = toDate(startCode), endDate = toDate(endCode);

        long diff = Math.abs(startDate.getTime() - endDate.getTime());
        double hour =  (diff / (60.0 * 60.0 * 1000.0));
        int duration = (int)(hour) * 2;
        if(hour - (int) hour > 0.0) {
            duration++;
        }
        return duration + 1;
    }

    int getAvgCode(int startCode, int endCode){
        List<Integer> codeList = new ArrayList<>();
        for(int i = startCode; i <= endCode; i = getNext(i)) codeList.add(i);
        return codeList.get(codeList.size() / 2);
    }

    int getAvgCodeFromDuration(int startCode, int duration){
        List<Integer> codeList = new ArrayList<>();
        int count = 0;
        for(int i = startCode; count < duration; i = getNext(i), count++) codeList.add(getNext(i));
        return codeList.get(codeList.size() / 2);
    }
}
