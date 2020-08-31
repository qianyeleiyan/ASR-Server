package org.codesdream.asr.component.time;


import org.checkerframework.checker.nullness.Opt;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

@Component
public class TimeRegionSeizeAllocator {

    @Resource
    private TimeAPMAllocRepository apmAllocRepository;

    public TimeAPMAlloc getAPMAlloc(String apmId){
        Optional<TimeAPMAlloc> apmAllocOptional = apmAllocRepository.findByApmId(apmId);
        if(!apmAllocOptional.isPresent()) throw new InnerDataTransmissionException(apmId);
        else return apmAllocOptional.get();
    }

    public void getPreferDurationPlan(TimeAppointTask appointTask, Integer duration, TimeRegion region){

    }
}
