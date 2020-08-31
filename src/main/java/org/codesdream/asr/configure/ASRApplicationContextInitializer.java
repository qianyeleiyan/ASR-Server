package org.codesdream.asr.configure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 服务端程序初始化检查
 */
@Slf4j
public class ASRApplicationContextInitializer implements ApplicationContextInitializer {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        log.info("EPApplicationContextInitializer Started");

    }
}
