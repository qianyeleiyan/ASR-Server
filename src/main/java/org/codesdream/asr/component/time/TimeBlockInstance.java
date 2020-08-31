package org.codesdream.asr.component.time;

import io.swagger.models.auth.In;
import lombok.Data;
import lombok.ToString;
import org.codesdream.asr.model.time.TimeBlock;

import java.io.Serializable;

@Data
public class TimeBlockInstance implements Comparable<TimeBlockInstance>, Serializable {

    private Integer code;

    private String APMId;

    private boolean enable;

    private boolean assigned;

    private boolean expired;

    private boolean emergency = false;

    private Integer timeBlockId = null;

    // 已被利用
    private boolean used = false;

    private boolean updated = false;

    public TimeBlockInstance(TimeBlock timeBlock){
        this.code = timeBlock.getCode();
        this.enable = timeBlock.getEnable();
        this.assigned = true;
        this.expired = false;
        this.timeBlockId = timeBlock.getId();
        this.APMId = timeBlock.getAPMId();
        this.emergency = timeBlock.getEmergency();
    }

    public TimeBlockInstance(Integer code){
        this.code = code;
        this.enable = true;
        this.assigned = false;
        this.expired = false;
    }

    public TimeBlock parseTimeBlock(Integer id){
        TimeBlock timeBlock = new TimeBlock();

        timeBlock.setId(id);
        timeBlock.setCode(this.code);
        timeBlock.setAPMId(this.APMId);
        timeBlock.setEmergency(this.emergency);
        timeBlock.setEnable(this.enable);
        timeBlock.setUsed(this.used);

        return timeBlock;
    }

    public int getStatus(){
        if(this.expired) return TimeBlockStatus.EXPIRED;
        else if (!this.enable) return TimeBlockStatus.DISABLED;
        else if(this.assigned) return TimeBlockStatus.ASSIGNED;
        else return TimeBlockStatus.FREE;
    }

    public boolean update(int current){
        if(this.code < current){
            this.expired = true;

            if(!this.used){
                // TODO 添加任务/计划有关的代码

            }

            return false;
        }
        else return true;
    }

    @Override
    public int compareTo(TimeBlockInstance o) {
        return this.code.compareTo(o.code);
    }
}
