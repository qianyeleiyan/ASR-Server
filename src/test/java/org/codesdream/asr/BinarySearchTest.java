package org.codesdream.asr;

import org.codesdream.asr.component.datamanager.BinarySearch;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
public class BinarySearchTest {
    @Test
    public void testBinarySearch() {
        List<Integer> a = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            a.add(i);
        }
        int lw = BinarySearch.lowerBound(a, 0, a.size(), 5);
        int up = BinarySearch.upperBound(a, 0, a.size(), 9);
        System.out.println(lw + "\n" + up);
    }
}
