package org.codesdream.asr.component.time;

import lombok.Data;
import org.checkerframework.checker.nullness.Opt;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.codesdream.asr.model.time.TimeBlock;
import org.codesdream.asr.model.time.TimeDisableLaw;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.codesdream.asr.repository.time.TimeBlockRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

// 时间管理处理器
@Data
public class TMProcessor {

    private final TimeRegionsPool regionsPool;

    private final TimeBlocksPool timeBlocksPool;

    private TimeBlockRepository blockRepository;

    private Integer userId;

    // 需要初始化的变量

    private transient TimeAPMAllocRepository apmAllocRepository;

    public TMProcessor(TimeRegionsPool regionsPool){
        this.regionsPool = regionsPool;
        this.timeBlocksPool = regionsPool.getTimeBlocksPool();
    }

    public boolean rollback(String apmId){

        Optional<TimeAPMAlloc> apmAllocOptional = apmAllocRepository.findByApmId(apmId);

        if(!apmAllocOptional.isPresent() || apmAllocOptional.get().getRollback()) return false;

        TimeAPMAlloc apmAlloc = apmAllocOptional.get();

        List<Integer> codeList = apmAlloc.getCodeList();

        Collections.sort(codeList);

        regionsPool.removeAssignedTimeRegion(codeList.get(0), codeList.get(codeList.size()-1));

        apmAlloc.setRollback(true);

        deleteTimeBlocks(codeList);

        apmAllocRepository.save(apmAlloc);

        return true;
    }

    private void deleteTimeBlocks(List<Integer> codes){
        Map<Integer, TimeBlockInstance> timeBlockInstanceMap = timeBlocksPool.getTimeBlockInstanceMap();
        for(Integer code : codes){
            blockRepository.deleteByCodeAndUserId(code, userId);
            timeBlockInstanceMap.remove(code);
        }
    }

    public void disableTimeBlocks(Iterable<TimeDisableLaw> timeDisableLaws){

        refresh(false);

        Map<Integer, List<Integer>> disableLawMap = new HashMap<>();

        for(TimeDisableLaw law : timeDisableLaws){
            disableLawMap.put(law.getDayOfWeek(), law.getScale());
        }

        timeBlocksPool.setDisableLawMap(disableLawMap);

        // 重新计算可用时间段
        regionsPool.clear();
        regionsPool.mergeTimeBlocks();
    }

    // 刷新数据库状态
    public void refresh(boolean merge){
        if(merge) {
            // 整理时间段表
            regionsPool.mergeFree();
            regionsPool.mergeAssigned();
        }

        // 删除未分配的时间块
        for(TimeBlockInstance blockInstance : timeBlocksPool.freeTimeBlockMap.values()){
            Optional<TimeBlock> timeBlockOptional =
                    blockRepository.findByCodeAndUserId(blockInstance.getCode(), userId);
            if(timeBlockOptional.isPresent())
                blockRepository.deleteByCodeAndUserId(blockInstance.getCode(), userId);
            timeBlocksPool.freeTimeBlockMap.remove(blockInstance.getCode());
        }

        // 更新已分配的时间块的状态
        for(TimeBlockInstance blockInstance : timeBlocksPool.timeBlockInstanceMap.values()){
            if(blockInstance.isUpdated()){
                Optional<TimeBlock> timeBlockOptional =
                        blockRepository.findByCodeAndUserId(blockInstance.getCode(), userId);

                TimeBlock timeBlock = null;

                if(!timeBlockOptional.isPresent()){
                    timeBlock = blockInstance.parseTimeBlock(null);
                }
                else {
                    timeBlock = blockInstance.parseTimeBlock(timeBlockOptional.get().getId());
                }
                timeBlock.setUserId(userId);

                blockRepository.save(timeBlock);

                blockInstance.setUpdated(false);
            }
        }
    }
}
