package org.codesdream.asr.component.time;

import org.codesdream.asr.model.time.TimeAPMAlloc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TimeAPMPlan implements Serializable, Comparable<TimeAPMPlan> {

    // 起始时间编号
    Integer startCode;

    // 终止时间编号
    Integer endCode;

    // 时长（时间块）
    Integer duration = 0;

    // 权值
    Float Feasibility;

    // 分配号
    String allocId = UUID.randomUUID().toString();


    public TimeAPMAlloc parseTimeAPMAlloc(){
        TimeAPMAlloc apmAlloc = new TimeAPMAlloc();

        apmAlloc.setApmId(this.allocId);
        apmAlloc.setDuration(this.duration);
        apmAlloc.setStartCode(this.startCode);
        apmAlloc.setWeight(this.Feasibility);

        // 占用的时间块编号列表
        List<Integer> codeList = new ArrayList<>();
        for(int i = startCode; i <= endCode; i++){
            codeList.add(i);
        }
        apmAlloc.setCodeList(codeList);

        return apmAlloc;
    }

    @Override
    public int compareTo(TimeAPMPlan o) {
        return this.startCode.compareTo(o.startCode);
    }
}
