package org.codesdream.asr.component.time;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.model.record.TATRecord;
import org.codesdream.asr.repository.record.TATRecordRepository;
import org.codesdream.asr.service.IAsyncService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@Slf4j
@Component
public class TimeSystemEngine {

    @Resource
    private IAsyncService asyncService;

    @Resource
    private TATRecordRepository tatRecordRepository;

    Map<String, Integer> allocTaskMap = new HashMap<>();

    public TimeSystemEngine(TATRecordRepository tatRecordRepository){

        this.setTatRecordRepository(tatRecordRepository);
        
        Iterable<TATRecord> tatRecords = tatRecordRepository.findAllByFinished(false);
        for(TATRecord tatRecord : tatRecords){
            allocTaskMap.put(tatRecord.getRequestId(), tatRecord.getUserId());
        }

        log.info("TimeSystemEngine Started.");
    }

    public synchronized void registerTimeAllocTask(Integer userId, String requestId){

        log.info(String.format("Get New Time Alloc Task Register: (%d, %s)", userId, requestId));

        allocTaskMap.put(requestId, userId);
        TATRecord tatRecord = new TATRecord();
        tatRecord.setUserId(userId);
        tatRecord.setRequestId(requestId);
        tatRecord.setFinished(false);
        tatRecordRepository.save(tatRecord);

    }

    @Scheduled(cron = "0/3 * * ? * *")
    public void process(){
        log.info("TimeSystemEngine Cycle.");
        log.info(String.format("Registered AllocTask Number: %d", allocTaskMap.size()));
        for(String requestId : allocTaskMap.keySet()) {

            Integer userId = allocTaskMap.get(requestId);

            log.info(String.format("Starting Processing Time Alloc Task: (%d, %s)", userId, requestId));
            asyncService.doAsyncProcessTimeAllocTask(userId, requestId);

            Optional<TATRecord> tatRecordOptional = tatRecordRepository.findByUserIdAndRequestId(userId, requestId);

            if(!tatRecordOptional.isPresent()) throw new InnerDataTransmissionException(requestId);
            TATRecord tatRecord = tatRecordOptional.get();
            tatRecord.setFinished(true);
            tatRecordRepository.save(tatRecord);

            allocTaskMap.remove(requestId);
        }
    }


}
