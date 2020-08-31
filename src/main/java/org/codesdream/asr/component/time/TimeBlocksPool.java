package org.codesdream.asr.component.time;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.codesdream.asr.model.time.TimeBlock;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@ToString
public class TimeBlocksPool implements Serializable {

    Map<Integer, TimeBlockInstance> timeBlockInstanceMap = new HashMap<>();

    Map<Integer, TimeBlockInstance> freeTimeBlockMap = new HashMap<>();

    Map<Integer, TimeBlockInstance> expiredTimeBlockMap = new HashMap<>();

    Map<Integer, List<Integer>> disableLawMap = new HashMap<>();

    public TimeBlocksPool(Iterable<TimeBlock> timeBlockList){
        for(TimeBlock timeBlock : timeBlockList){
            TimeBlockInstance timeBlockInstance = new TimeBlockInstance(timeBlock);
            timeBlockInstanceMap.put(timeBlockInstance.getCode(), timeBlockInstance);
        }
    }

    public int getTimeBlockStatus(Integer code){
        TimeBlockInstance instance = timeBlockInstanceMap.get(code);
        if(instance != null) return instance.getStatus();
        else return TimeBlockStatus.FREE;
    }

    public TimeBlockInstance getTimeBlockInstance(Integer code, Integer dayOfWeek){

        // 禁止时间块检测
        List<Integer> disabledScales = disableLawMap.get(dayOfWeek);
        if(disabledScales != null){
            int scale = code % 100;
            if(disabledScales.contains(scale)) return null;
        }

        return getTimeBlockInstance(code);
    }

    public TimeBlockInstance getTimeBlockInstance(Integer code){

        TimeBlockInstance instance = timeBlockInstanceMap.get(code);
        if(instance != null) return instance;
        else {
            instance = freeTimeBlockMap.get(code);
            if(instance != null) return instance;
            return new TimeBlockInstance(code);
        }
    }

    // 需要预先填入分配信息
    public void assignedTimeBlock(TimeBlockInstance instance, String apmId){
        if(instance.getStatus() == TimeBlockStatus.FREE) {
            instance.setAssigned(true);
            instance.setAPMId(apmId);
            instance.setUpdated(true);
            timeBlockInstanceMap.put(instance.getCode(), instance);
        }
    }

    public void expiredTimeBlock(Integer code){
        TimeBlockInstance blockInstance = timeBlockInstanceMap.get(code);
        if(blockInstance != null){
            expiredTimeBlockMap.put(code, blockInstance);
        }
    }

    // 擦除分配信息
    public void freeTimeBlock(TimeBlockInstance instance){
        if(instance.getStatus() == TimeBlockStatus.ASSIGNED) {
            instance.setAPMId(null);
            instance.setAssigned(false);
            instance.setUpdated(true);
            timeBlockInstanceMap.remove(instance.getCode());
            freeTimeBlockMap.put(instance.getCode(), instance);
        }
    }
}
