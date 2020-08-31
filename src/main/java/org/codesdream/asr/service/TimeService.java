package org.codesdream.asr.service;

import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.task.Appointment;
import org.codesdream.asr.component.time.TCGObjectPool;
import org.codesdream.asr.component.time.TimeCalculateGroup;
import org.codesdream.asr.exception.innerservererror.HandlingErrorsException;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.model.record.TATRecord;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.codesdream.asr.model.time.TimeDisableLaw;
import org.codesdream.asr.repository.record.TATRecordRepository;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.codesdream.asr.repository.time.TimeDisableLawRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TimeService implements ITimeService {

    @Resource
    private TCGObjectPool tcgObjectPool;

    @Resource
    private TATRecordRepository tatRecordRepository;

    @Resource
    private TimeAPMAllocRepository apmAllocRepository;

    @Resource
    private TimeDisableLawRepository disableLawRepository;

    @Override
    public String createAllocTask(Integer userId, Appointment appointment) {
        try {
            log.info(String.format("Creating New Appointment Task For %s...", userId));
            TimeCalculateGroup group = tcgObjectPool.borrowObject(userId);
            String requestId = group.getRegionsManager().allocTime(appointment);
            tcgObjectPool.returnObject(userId, group);
            log.info(String.format("New Appointment Task Creation For %d Done (RequestId: %s).", userId, requestId));
            return requestId;
        } catch (Exception e) {
            e.printStackTrace();
            throw new HandlingErrorsException(e.getMessage());
        }
    }

    @Override
    public boolean rollbackAllocTask(Integer userId, String allocId) {
        try {
            TimeCalculateGroup group = tcgObjectPool.borrowObject(userId);
            boolean success = group.getRegionsManager().rollback(allocId);
            tcgObjectPool.returnObject(userId, group);
            return success;
        } catch (Exception e) {
            throw new HandlingErrorsException(e.getMessage());
        }
    }

    @Override
    public List<TimeAPMAlloc> getAllocTaskResult(Integer userId, String requestId) {
        Optional<TATRecord> tatRecordOptional = tatRecordRepository.findByUserIdAndRequestId(userId, requestId);
        if(!tatRecordOptional.isPresent()) throw new InnerDataTransmissionException(requestId);

        if(!tatRecordOptional.get().isFinished()) return null;
        else{
            Iterable<TimeAPMAlloc> apmAllocs = apmAllocRepository.findAllByRequestId(requestId);

            List<TimeAPMAlloc> apmAllocList = new ArrayList<>();

            for(TimeAPMAlloc apmAlloc : apmAllocs){
                apmAllocList.add(apmAlloc);
            }

            return apmAllocList;
        }
    }

    @Override
    public void updateTimeBlockDisabledLaw(Integer userId) {
        try {
            TimeCalculateGroup group = tcgObjectPool.borrowObject(userId);
            Iterable<TimeDisableLaw> disableLaws = disableLawRepository.findAllByUserId(userId);
            group.getTmProcessor().disableTimeBlocks(disableLaws);
            tcgObjectPool.returnObject(userId, group);
        } catch (Exception e) {
            throw new HandlingErrorsException(e.getMessage());
        }
    }

}
