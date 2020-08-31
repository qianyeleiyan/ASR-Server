package org.codesdream.asr.component.auth;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 验证时间戳验证器
 */
@Component
public class TimestampExpiredChecker {

    /**
     * 检查时间戳是否在某个最大有效时间前
     * @param timestamp 时间戳字符串
     * @param seconds 最大有效时间
     * @return 布尔值
     */
    public boolean checkTimestampBeforeMaxTime(String timestamp, int seconds){
        Date timestampDate = new Date(Long.parseLong(timestamp));
        long currentTime = System.currentTimeMillis();
        Date maxDate = new Date(currentTime + seconds * 1000);
        return timestampDate.before(maxDate);
    }

    /**
     * 检查时间戳是否在某个最大有效时间前
     * @param date 时间对象
     * @param seconds 最大做大有效时间
     * @return 布尔值
     */
    public boolean checkDateBeforeMaxTime(Date date, int seconds){
        long currentTime = System.currentTimeMillis();
        Date maxDate = new Date(currentTime + seconds * 1000);
        return date.before(maxDate);
    }

    /**
     * 检查时间戳是否在某个设置的时间前
     * @param date 时间对象
     * @param seconds 设置时间
     * @return 布尔值
     */
    public boolean checkDateBeforeDeterminedTime(Date date, int seconds){
        long currentTime = System.currentTimeMillis();
        Date maxDate = new Date(currentTime - seconds * 1000);
        return date.before(maxDate);
    }

}
