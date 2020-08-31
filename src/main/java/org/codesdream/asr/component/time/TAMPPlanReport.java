package org.codesdream.asr.component.time;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TAMPPlanReport implements Comparable<TAMPPlanReport> {

    Map<Integer, TimeRegion> timeRegionMap;

    List<TimeAPMPlan> timeAPMPlans;

    List<TimeBlockInstance> timeBlockInstances;

    Map<Integer, String> tbAPMIdMap = new HashMap<>();

    Float weight = 0.0f;

    // 完全成功状态
    boolean success = false;

    @Override
    public int compareTo(TAMPPlanReport o) {
        if(o.success && !this.success){
            return -1;
        }
        else if(!o.success && this.success){
            return  1;
        }
        else{
            return this.weight.compareTo(o.weight);
        }
    }
}
