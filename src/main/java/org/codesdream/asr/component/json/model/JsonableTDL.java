package org.codesdream.asr.component.json.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codesdream.asr.model.time.TimeDisableLaw;

import java.util.*;

@Data
@NoArgsConstructor
public class JsonableTDL {
    Integer id;
    Integer dayOfWeek;
    Set<JsonablePeriod> periods = new HashSet<>();

    public JsonableTDL(TimeDisableLaw disableLaw){
        this.id = disableLaw.getId();
        this.dayOfWeek = disableLaw.getDayOfWeek();
        Integer lastTimeScale = -1;
        List<Integer> period = new ArrayList<>();

        List<Integer> scales = new ArrayList<>(disableLaw.getScale());
        Collections.sort(scales);

        for(Integer scale : scales){
            if (!lastTimeScale.equals(-1) && !lastTimeScale.equals(scale - 1)) {
                periods.add(new JsonablePeriod(period.get(0), period.get(period.size() - 1)));
                period.clear();
            }
            period.add(scale);
            lastTimeScale = scale;
        }

        if(period.size() > 0) periods.add(new JsonablePeriod(period.get(0), period.get(period.size() - 1)));
    }

    public TimeDisableLaw parseModel(Integer userId){
        TimeDisableLaw disableLaw = new TimeDisableLaw();
        disableLaw.setId(this.id);
        disableLaw.setDayOfWeek(this.dayOfWeek);
        disableLaw.setUserId(userId);

        Set<Integer> scaleSet = new HashSet<>();
        for(JsonablePeriod period : periods){
            for(int i = period.startScale; i <= period.endScale; i++) {
                if(i < 0 || i > 47) throw new IllegalArgumentException(Integer.toString(i));
                scaleSet.add(i);
            }
        }
        disableLaw.getScale().addAll(scaleSet);

        return disableLaw;
    }
}
