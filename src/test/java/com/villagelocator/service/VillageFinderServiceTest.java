package com.villagelocator.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VillageFinderServiceTest {

    @Test
    void findVillagesReturnsDistanceSortedResults() {
        VillageFinderService service = new VillageFinderService();

        List<Map<String, Integer>> villages = service.findVillages(12345L, 0, 0, 4000);

        assertFalse(villages.isEmpty());
        long previousDistance = -1L;
        for (Map<String, Integer> village : villages) {
            long distance = distanceSquared(village.get("x"), village.get("z"), 0, 0);
            assertTrue(distance >= previousDistance);
            previousDistance = distance;
        }
    }

    private long distanceSquared(int x1, int z1, int x2, int z2) {
        long dx = (long) x1 - x2;
        long dz = (long) z1 - z2;
        return dx * dx + dz * dz;
    }
}
