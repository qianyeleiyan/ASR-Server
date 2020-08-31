package org.codesdream.asr.component.time;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codesdream.asr.component.datamanager.ObjectStreamGenerator;
import org.codesdream.asr.exception.innerservererror.InnerDataTransmissionException;
import org.codesdream.asr.model.time.TimeBlock;
import org.codesdream.asr.model.time.TimeDisableLaw;
import org.codesdream.asr.model.user.User;
import org.codesdream.asr.model.user.UserBuffer;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.codesdream.asr.repository.time.TimeBlockRepository;
import org.codesdream.asr.repository.time.TimeDisableLawRepository;
import org.codesdream.asr.repository.user.UserRepository;
import org.codesdream.asr.service.FileService;
import org.codesdream.asr.service.IFileService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.swing.text.html.Option;
import java.sql.Time;
import java.util.*;
import org.slf4j.Logger;


@Data
public class TimeCalculateGroup{

    private TimeBlocksPool blocksPool;

    private TimeRegionsPool regionsPool;

    private TATaskProcessor taTaskProcessor;

    private TMProcessor tmProcessor;

    private TimeRegionsManager regionsManager;

    private Integer userId;

    private TimeBlockRepository blockRepository;

    private UserRepository userRepository;

    private IFileService fileService;

    private ObjectStreamGenerator streamGenerator;

    private TimeRegionFreeAllocator freeAllocator;

    private TimeAPMAllocRepository apmAllocRepository;

    private TimeSystemEngine timeSystemEngine;

    private TimeBlockCodeGenerator codeGenerator;

    private WeightToolBox weightToolBox;

    private TimeScaleStatus scaleStatus;

    private TimeDisableLawRepository lawRepository;

    private RegionToolBox regionToolBox;

    private Logger log;


    public TimeCalculateGroup(Integer userId){

        this.userId = userId;

    }

    // 初始化s
    public void init(){

        log.info(String.format("TimeCalculateGroup For %d Init Starting...", userId));

        Optional<User> userOptional = userRepository.findById(userId);

        if(!userOptional.isPresent()) throw new InnerDataTransmissionException(userId.toString());

        User user = userOptional.get();
        UserBuffer buffer = user.getUserBuffer();

        if(buffer == null){
            user.setUserBuffer(new UserBuffer());
            user = userRepository.save(user);
            buffer = user.getUserBuffer();
        }

        // 未创建缓存文件
        if(!buffer.isActive()
                || buffer.getTBPBufferFileId() == null
                || buffer.getTAPBufferFileId() == null
                || buffer.getTRPBufferFileId() == null) {


            log.info(String.format("Init TimeCalculateGroup For %d WITHOUT Buffer...", userId));

            Iterable<TimeBlock> timeBlocks = blockRepository.findAllByUserId(userId);

            // 时间块池
            blocksPool = new TimeBlocksPool(timeBlocks);

            Iterable<TimeDisableLaw> timeDisableLaws = lawRepository.findAllByUserId(userId);
            Map<Integer, List<Integer>> disableLawMap = new HashMap<>();

            for(TimeDisableLaw law : timeDisableLaws){
                disableLawMap.put(law.getDayOfWeek(), law.getScale());
            }

            log.info(String.format("TimeBlocksPool For %d Init Done.", userId));

            blocksPool.setDisableLawMap(disableLawMap);

            // 时间段池
            regionsPool = new TimeRegionsPool(blocksPool);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            calendar.add(Calendar.YEAR, +1);

            regionsPool.setMaxCode(codeGenerator.get(calendar));

            regionsPool.setCodeGenerator(codeGenerator);
            regionsPool.setScaleStatus(scaleStatus);

            log.info(String.format("TimeRegionPool For %d Start Merging...", userId));

            // 执行初始化合并操作
            regionsPool.mergeTimeBlocks();

            log.info(String.format("TimeRegionPool Fro %d Free Regions Number %d.",
                    userId, regionsPool.getFreeRegions().size()));
            log.info(String.format("TimeRegionPool For %d Merging Done.", userId));


            taTaskProcessor = new TATaskProcessor(userId ,regionsPool);
            taTaskProcessor.setLog(log);

        }
        else{

            log.info(String.format("Init TimeCalculateGroup For %d WITH Buffer...", userId));

            // 读取缓存文件
            blocksPool = (TimeBlocksPool) streamGenerator.getObject(fileService.getFile(buffer.getTBPBufferFileId()));
            regionsPool = (TimeRegionsPool) streamGenerator.getObject(fileService.getFile(buffer.getTRPBufferFileId()));

            log.info(String.format("Read Buffer Files For %d Done..", userId));

            // 任务分配状态处理器
            taTaskProcessor = (TATaskProcessor) streamGenerator.getObject(fileService.getFile(buffer.getTAPBufferFileId()));

            regionsPool.bufferedInit(codeGenerator);

            taTaskProcessor.setTimeBlocksPool(blocksPool);
            taTaskProcessor.setRegionsPool(regionsPool);

            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
            calendar.add(Calendar.YEAR, +1);

            int newMaxCode = codeGenerator.get(calendar);


            // 计算新的空闲时间段
            TimeRegion newRegion = new TimeRegion(codeGenerator);

            int newRegionStartCode = codeGenerator.getNext(regionsPool.getMaxCode());
            Calendar cycleCalendar = codeGenerator.getCalendarFromCode(newRegionStartCode);
            for(int i = newRegionStartCode; i <= newMaxCode; i = codeGenerator.getNext(cycleCalendar)){
                newRegion.simpleMergeBack(blocksPool.getTimeBlockInstance(i));
            }

            // 更新空闲时间段表
            if(newRegion.getTimeBlockInstanceMap().size() > 0){
                regionsPool.getFreeRegions().put(newRegion.getMinCode(), newRegion);
                regionsPool.mergeFree();
            }

            regionsPool.setMaxCode(codeGenerator.get(calendar));

            regionsPool.setCodeGenerator(codeGenerator);
            regionsPool.setScaleStatus(scaleStatus);

        }

        tmProcessor = new TMProcessor(regionsPool);
        tmProcessor.setBlockRepository(blockRepository);
        tmProcessor.setUserId(userId);

        regionsManager = new TimeRegionsManager(taTaskProcessor, tmProcessor);
        regionsManager.setCodeGenerator(codeGenerator);

        taTaskProcessor.setRegionAllocator(freeAllocator);
        taTaskProcessor.setApmAllocRepository(apmAllocRepository);
        taTaskProcessor.setTimeSystemEngine(timeSystemEngine);
        taTaskProcessor.setCodeGenerator(codeGenerator);
        taTaskProcessor.setRegionToolBox(regionToolBox);

        regionsPool.setRegionToolBox(regionToolBox);

        tmProcessor.setApmAllocRepository(apmAllocRepository);

        log.info(String.format("TimeCalculateGroup For %d Init Finished.", userId));
    }

    public void update(){

        log.info(String.format("TimeCalculateGroup For %d Update Starting...", userId));

        // 执行过期检查
        getRegionsPool().update(codeGenerator.getCurrent());

        log.info(String.format("TimeCalculateGroup For %d Update Scanning Done.", userId));

        // 从数据库中清除已过期的时间块
        for(TimeBlockInstance instance : getBlocksPool().getExpiredTimeBlockMap().values()){
            if(instance.getTimeBlockId() != null){
                log.info(String.format("Deleting Expired TimeBlock Code: %d", instance.getCode()));
                blockRepository.delete(instance.parseTimeBlock(instance.getTimeBlockId()));
            }
        }

       getBlocksPool().getExpiredTimeBlockMap().clear();

        log.info(String.format("Clearing Expired TimeBlockMap For %d Done", userId));
    }

    public void refresh(){
        tmProcessor.refresh(true);
    }

    public void buffered(){

        log.info(String.format("Creating Buffer For %d...", userId));
        Optional<User> userOptional = userRepository.findById(userId);

        if(!userOptional.isPresent()) throw new InnerDataTransmissionException(userId.toString());

        User user = userOptional.get();
        UserBuffer buffer = user.getUserBuffer();

        // 写入文件缓存
        buffer.setTBPBufferFileId(fileService.saveFile(
                String.format("TBP %d",userId),
                "Java Object",
                streamGenerator.getSteam(this.blocksPool)));

        buffer.setTRPBufferFileId(fileService.saveFile(
                String.format("TRP %d",userId),
                "Java Object",
                streamGenerator.getSteam(this.regionsPool)));

        buffer.setTAPBufferFileId(fileService.saveFile(
                String.format("TAP %d",userId),
                "Java Object",
                streamGenerator.getSteam(this.taTaskProcessor)));

        buffer.setActive(true);

        userRepository.save(user);

        log.info(String.format("Buffer Creation For %d Done.", userId));
    }

}
