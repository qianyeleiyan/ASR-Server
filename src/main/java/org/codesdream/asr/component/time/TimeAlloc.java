package org.codesdream.asr.component.time;

import java.util.ArrayList;
import java.util.List;

public class TimeAlloc {

    Integer taskId;

    boolean ifAlloc = false;

    boolean fullAlloc = false;

    Integer allocatedTimeBlock = 0;

    Integer neededTimeBlock = 0;

    List<TimeAllocRegion> allocRegions = new ArrayList<>();
}
