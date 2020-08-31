package org.codesdream.asr.configure;

import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.checkerframework.checker.units.qual.C;
import org.codesdream.asr.component.time.TimeCalculateGroup;
import org.springframework.stereotype.Component;

@Component
public class TCGPoolConfig extends GenericKeyedObjectPoolConfig<TimeCalculateGroup> {

    public TCGPoolConfig(){
        super();
        setLifo(false);
        setMaxWaitMillis(-1);
        setJmxEnabled(false);
        setMaxTotal(128);
        setMaxIdlePerKey(1);
        setMinIdlePerKey(0);
        setTestOnBorrow(true);
        setTestOnReturn(true);
    }
}
