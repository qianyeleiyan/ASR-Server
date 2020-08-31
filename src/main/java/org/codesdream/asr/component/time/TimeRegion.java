package org.codesdream.asr.component.time;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class TimeRegion implements Comparable<TimeRegion>, Serializable {

    private transient TimeBlockCodeGenerator codeGenerator;

    Map<Integer, TimeBlockInstance> timeBlockInstanceMap = new HashMap<>();

    Integer maxCode = -1;

    Integer minCode = 0;

    public TimeRegion(TimeBlockCodeGenerator codeGenerator){
        this.codeGenerator = codeGenerator;
    }

    // 合并时间块
    public void merge(TimeBlockInstance timeBlockInstance){
        if(isNeighbour(timeBlockInstance)){
            timeBlockInstanceMap.putIfAbsent(timeBlockInstance.getCode(), timeBlockInstance);
            updateBoundary();
        }
    }

    void simpleMergeBack(TimeBlockInstance timeBlockInstance){
        timeBlockInstanceMap.putIfAbsent(timeBlockInstance.getCode(), timeBlockInstance);
        maxCode = timeBlockInstance.getCode();
        if(minCode == 0) minCode = maxCode;
    }

    // 更新边界
    private void updateBoundary(){
        if(timeBlockInstanceMap.size() == 0){
            maxCode =-1;
            minCode = 0;
            return;
        }
        Set<Integer> codeSet = timeBlockInstanceMap.keySet();
        int instanceMax = Collections.max(codeSet);
        int instanceMin = Collections.min(codeSet);

        if(instanceMax > maxCode || maxCode == -1){
            maxCode = instanceMax;
        }
        if(instanceMin < minCode || minCode == 0) {
            minCode = instanceMin;
        }
    }

    public int size(){
        return this.timeBlockInstanceMap.size();
    }

    // 合并时间段
    public void merge(TimeRegion timeRegion){
        if(isNeighbour(timeRegion)) {
            timeBlockInstanceMap.putAll(timeRegion.getTimeBlockInstanceMap());
            updateBoundary();
        }
    }

    // 检查时间段是否相邻
    public boolean isNeighbour(TimeRegion timeRegion){
        return maxCode == -1
                || codeGenerator.getNext(timeRegion.maxCode) == this.minCode
                || codeGenerator.getNext(this.maxCode) == timeRegion.minCode;

    }

    // 检查时间块是否相邻
    public boolean isNeighbour(TimeBlockInstance blockInstance){
        return maxCode == -1
                || codeGenerator.getNext(blockInstance.getCode()) - this.minCode == 0
                || codeGenerator.getNext(this.maxCode) - blockInstance.getCode() == 0;
    }

    // 更新
    public int update(int current){
        List<TimeBlockInstance> timeBlockInstances = new ArrayList<>(timeBlockInstanceMap.values());

        Collections.sort(timeBlockInstances);

        Integer maxDeleteCode = -1;

        for(TimeBlockInstance instance : timeBlockInstances){
            if(!instance.update(current)){
                timeBlockInstanceMap.remove(instance.getCode());
                maxDeleteCode = instance.getCode();
            }
            else break;
        }

        updateBoundary();

        return maxDeleteCode;
    }


    @Override
    public int compareTo(TimeRegion o) {
        return this.minCode.compareTo(o.minCode);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        TimeRegion region = new TimeRegion(codeGenerator);
        region.minCode = this.minCode;
        region.maxCode = this.maxCode;
        region.timeBlockInstanceMap = this.timeBlockInstanceMap;
        return region;
    }

    public int getDuration(){
        return maxCode - minCode + 1;
    }
}
