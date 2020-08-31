package org.codesdream.asr.component.datamanager;

import org.codesdream.asr.component.json.model.JsonablePlanResult;
import org.codesdream.asr.component.json.model.JsonableTaskResult;
import org.codesdream.asr.component.time.TimeBlockCodeGenerator;
import org.codesdream.asr.model.time.TimeAPMAlloc;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.List;

@Component
public class JsonableResultGenerator {


    @Resource
    TimeBlockCodeGenerator timeBlockCodeGenerator;

    public void generateJsonableTaskResults(Integer taskId, List<JsonableTaskResult> jsonableTaskResults,
                                            TimeAPMAlloc timeAPMAlloc, Integer status) {
        JsonableTaskResult jsonableTaskResult = new JsonableTaskResult();
        jsonableTaskResult.setBegin(timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jsonableTaskResult.getBegin());
        calendar.add(Calendar.MINUTE, timeAPMAlloc.getDuration() * 30);
        jsonableTaskResult.setEnd(calendar.getTime());
        jsonableTaskResult.setTaskId(taskId);
        jsonableTaskResult.setStatus(status);
        jsonableTaskResults.add(jsonableTaskResult);
    }

    public void generateJsonablePlanResults(Integer taskId, List<JsonablePlanResult> jsonablePlanResults,
                                            TimeAPMAlloc timeAPMAlloc, Integer status) {
        JsonablePlanResult jsonablePlanResult = new JsonablePlanResult();
        jsonablePlanResult.setBegin(timeBlockCodeGenerator.toDate(timeAPMAlloc.getStartCode()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(jsonablePlanResult.getBegin());
        calendar.add(Calendar.MINUTE, timeAPMAlloc.getDuration() * 30);
        jsonablePlanResult.setEnd(calendar.getTime());
        jsonablePlanResult.setTaskId(taskId);
        jsonablePlanResult.setStatus(status);
        jsonablePlanResults.add(jsonablePlanResult);
    }
}
