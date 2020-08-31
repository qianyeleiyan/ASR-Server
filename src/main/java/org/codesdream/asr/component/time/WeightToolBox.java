package org.codesdream.asr.component.time;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.print.Doc;
import java.util.List;

@Component
public class WeightToolBox {

    @Resource
    private TimeBlockCodeGenerator codeGenerator;

    @Resource
    private TimeScaleStatus scaleStatus;

    // 偏好时段权重
    public Float preferTimeWeight(Integer preferenceTime, Integer code){
        if(scaleStatus.getCodeScaleStatus(code) == preferenceTime){
            return 5.0f;
        }
        else return 1.0f;
    }

    // 拖延权重
    public Float earlyLateWeight(Integer maxNum, Integer lastNum, Integer code){
        float x = codeGenerator.getDuration(codeGenerator.getCurrent(), code);
        float m = maxNum;
        float a = -m / lastNum;
        return  a * x + m;
    }

    // 截止时间权重
    public Float deadlineWeight(Integer endCode, Integer code){
        int x = codeGenerator.getDuration(code, endCode);
        float k3 = 0.2f * x + 3.2f;
        float k2 = (float) Math.log(0.1f *(x+ 3.0f));
        float k1 = (float) Math.pow(5.0f * x + 3.0f, 2);
        float k0 = (float) Math.log(k1);
        final double pow = Math.pow(k0, Math.pow(k2, k3));
        float r = (float) pow;
        if(Float.isNaN(r)) return 8.0f;
        return 8.0f / (float) pow;
    }

    public Float calculateAvgWeightForAPMPlan(List<TimeAPMPlan> timeAPMPlans){
        float totalWeight = 0.0f, avgWeight;

        for(TimeAPMPlan apmPlan : timeAPMPlans){
            totalWeight += apmPlan.Feasibility;
        }

        // 计算平均权值
        avgWeight = totalWeight / timeAPMPlans.size();

        return avgWeight;

    }

    public Float calculateAvgWeight(List<Float> weights){
        float totalWeight = 0.0f, avgWeight;

        for(Float weight : weights){
            totalWeight += weight;
        }

        // 计算平均权值
        avgWeight = totalWeight / weights.size();

        return avgWeight;

    }
}
