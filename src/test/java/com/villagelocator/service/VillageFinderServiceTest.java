package com.villagelocator.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    @Test
    void candidateChunkUsesMinecraftVillageRegionMath() throws Exception {
        VillageFinderService service = new VillageFinderService();
        Method method = VillageFinderService.class.getDeclaredMethod(
            "getCandidateChunkInRegion", long.class, int.class, int.class
        );
        method.setAccessible(true);

        long seed = 12345L;
        int regionX = -3;
        int regionZ = 7;
        int[] candidate = (int[]) method.invoke(service, seed, regionX, regionZ);

        Random random = new Random(
            seed
                + (long) regionX * 341873128712L
                + (long) regionZ * 132897987541L
                + 10387312L
        );

        assertArrayEquals(new int[] {
            regionX * 32 + random.nextInt(24),
            regionZ * 32 + random.nextInt(24)
        }, candidate);
    }

    private long distanceSquared(int x1, int z1, int x2, int z2) {
        long dx = (long) x1 - x2;
        long dz = (long) z1 - z2;
        return dx * dx + dz * dz;
    }
}
