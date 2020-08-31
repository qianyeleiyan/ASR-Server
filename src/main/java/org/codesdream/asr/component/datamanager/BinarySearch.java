package org.codesdream.asr.component.datamanager;

import java.util.List;

public class BinarySearch {

    public static int lowerBound(List<Integer> nums, int l, int r, Integer k) {
        while (l < r) {
            int mid = (l + r) >> 1;
            if (nums.get(mid) >= k) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }
        return l;
    }

    public static int upperBound(List<Integer> nums, int l, int r, Integer k) {
        while (l < r) {
            int mid = (l + r) >> 1;
            if (nums.get(mid) <= k) {
                l = mid + 1;
            } else r = mid;
        }
        return l;
    }
}
