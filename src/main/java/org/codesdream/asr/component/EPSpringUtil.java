package org.codesdream.asr.component;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 手动获得Bean的工具类
 */
@Component
public class EPSpringUtil {
    @Resource
    private ApplicationContext applicationContext;

    public <T> T getBean(Class<T> tClass){
        return applicationContext.getBean(tClass);
    }

}
