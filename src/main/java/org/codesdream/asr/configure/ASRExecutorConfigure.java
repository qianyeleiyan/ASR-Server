package org.codesdream.asr.configure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 */
@Slf4j
@Configuration
public class ASRExecutorConfigure {

    @Bean(value = "PaPoolExecutor")
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor executor =new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(4);
        // 最大线程数
        executor.setMaxPoolSize(8);
        // 队列大小
        executor.setQueueCapacity(100);
        // 空闲线程等待工作的超时时间
        executor.setKeepAliveSeconds(60);
        // 线程池中的线程的名称前缀
        executor.setThreadNamePrefix("pa-pool");

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 线程池初始化
        executor.initialize();

        return executor;
    }
}
