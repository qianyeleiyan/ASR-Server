package org.codesdream.asr.service;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.time.TCGObjectPool;
import org.codesdream.asr.component.time.TimeCalculateGroup;
import org.codesdream.asr.exception.innerservererror.HandlingErrorsException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AsyncService implements IAsyncService {

    @Resource
    private TCGObjectPool tcgObjectPool;

    @Override
    @Async("PaPoolExecutor")
    public void doAsyncProcessTimeAllocTask(Integer userId, String requestId) {
        try {
            log.info(String.format("Async Process TimeAllocTask Thread Running: (%d, %s)", userId, requestId));
            log.info(String.format("ObjectPool: CA,%d NI, %d DS, %d",tcgObjectPool.getCreatedCount(),
                    tcgObjectPool.getNumIdle(userId),
                    tcgObjectPool.getDestroyedCount()));

            TimeCalculateGroup group = tcgObjectPool.borrowObject(userId);
            if(!group.getTaTaskProcessor().runAllocTask(requestId)) throw new HandlingErrorsException(requestId);
            tcgObjectPool.returnObject(userId, group);
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlingErrorsException(e.getMessage());
        }
    }
}
