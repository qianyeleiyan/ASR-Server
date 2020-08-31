package org.codesdream.asr.component.time;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

public class TCGObjectPool extends GenericKeyedObjectPool<Integer, TimeCalculateGroup> {

    public TCGObjectPool(TCGFactory factory,
                         GenericKeyedObjectPoolConfig<TimeCalculateGroup> config) {
        super(factory, config);
    }
}
