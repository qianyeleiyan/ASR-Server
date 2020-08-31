package org.codesdream.asr;

import org.codesdream.asr.configure.ASRApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class AsrApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AsrApplication.class);
        // 添加启动检查
        application.addInitializers(new ASRApplicationContextInitializer());
        application.run(args);
    }

}
