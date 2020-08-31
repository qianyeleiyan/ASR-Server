package org.codesdream.asr.component.time;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.codesdream.asr.component.datamanager.ObjectStreamGenerator;
import org.codesdream.asr.repository.time.TimeAPMAllocRepository;
import org.codesdream.asr.repository.time.TimeBlockRepository;
import org.codesdream.asr.repository.time.TimeDisableLawRepository;
import org.codesdream.asr.repository.user.UserRepository;
import org.codesdream.asr.service.IFileService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class TCGFactory implements KeyedPooledObjectFactory<Integer, TimeCalculateGroup> {

    @Resource
    private TimeBlockRepository blockRepository;

    @Resource
    private UserRepository userRepository;

    @Resource
    private IFileService fileService;

    @Resource
    private ObjectStreamGenerator streamGenerator;

    @Resource
    private TimeRegionFreeAllocator freeAllocator;

    @Resource
    private TimeAPMAllocRepository apmAllocRepository;

    @Resource
    private TimeSystemEngine timeSystemEngine;

    @Resource
    private TimeBlockCodeGenerator codeGenerator;

    @Resource
    private WeightToolBox weightToolBox;

    @Resource
    private TimeScaleStatus scaleStatus;

    @Resource
    private TimeDisableLawRepository lawRepository;

    @Resource
    private RegionToolBox regionToolBox;

    @Override
    public PooledObject<TimeCalculateGroup> makeObject(Integer userId) throws Exception {
        TimeCalculateGroup timeCalculateGroup =  new TimeCalculateGroup(userId);

        // 注入依赖模块
        timeCalculateGroup.setUserRepository(userRepository);
        timeCalculateGroup.setBlockRepository(blockRepository);
        timeCalculateGroup.setApmAllocRepository(apmAllocRepository);
        timeCalculateGroup.setFileService(fileService);
        timeCalculateGroup.setCodeGenerator(codeGenerator);
        timeCalculateGroup.setFreeAllocator(freeAllocator);
        timeCalculateGroup.setStreamGenerator(streamGenerator);
        timeCalculateGroup.setWeightToolBox(weightToolBox);
        timeCalculateGroup.setScaleStatus(scaleStatus);
        timeCalculateGroup.setLawRepository(lawRepository);
        timeCalculateGroup.setRegionToolBox(regionToolBox);
        timeCalculateGroup.setTimeSystemEngine(timeSystemEngine);
        timeCalculateGroup.setLog(log);

        // 执行必要的初始化步骤
        timeCalculateGroup.init();

        return new DefaultPooledObject<>(timeCalculateGroup);
    }

    @Override
    public void destroyObject(Integer userId, PooledObject<TimeCalculateGroup> pooledObject) throws Exception {
        // 缓存数据
//        pooledObject.getObject().buffered();
    }

    @Override
    public boolean validateObject(Integer userId, PooledObject<TimeCalculateGroup> pooledObject) {
        return pooledObject.getObject() != null
                && pooledObject.getObject().getUserId().equals(userId);
    }

    @Override
    public void activateObject(Integer userId, PooledObject<TimeCalculateGroup> pooledObject) throws Exception {

        log.info(String.format("TimeCalculateGroup Active: %d", userId));

        pooledObject.getObject().update();

    }

    @Override
    public void passivateObject(Integer userId, PooledObject<TimeCalculateGroup> pooledObject) throws Exception {

        log.info(String.format("TimeCalculateGroup Passivate: %d", userId));

        pooledObject.getObject().refresh();

    }
}
