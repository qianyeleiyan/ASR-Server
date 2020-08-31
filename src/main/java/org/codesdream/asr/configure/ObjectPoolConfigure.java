package org.codesdream.asr.configure;

import org.codesdream.asr.component.time.TCGFactory;
import org.codesdream.asr.component.time.TCGObjectPool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Configuration
public class ObjectPoolConfigure {

    @Resource
    private TCGPoolConfig tcgPoolConfig;

    @Resource
    private TCGFactory tcgFactory;

    private TCGObjectPool pool;

    @Bean
    @ConditionalOnClass(TCGFactory.class)
    protected TCGObjectPool faceSDKPool() {
        return new TCGObjectPool(tcgFactory, tcgPoolConfig);
    }

    @PreDestroy
    public void destroy() {
        if (pool != null) {
            pool.close();
        }
    }
}