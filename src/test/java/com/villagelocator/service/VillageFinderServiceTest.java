package com.villagelocator.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VillageFinderServiceTest {

    @Test
    void findVillagesReturnsValidLocations() {
        VillageFinderService service = new VillageFinderService();

        // Search in a 1000x1000 chunk region
        List<VillageFinderService.VillageLocation> villages = 
            service.findVillages(12345L, 0, 100, 0, 100);

        assertNotNull(villages);
        assertFalse(villages.isEmpty(), "Should find villages in this region");
        
        // Verify each village has valid coordinates
        for (VillageFinderService.VillageLocation village : villages) {
            assertTrue(village.blockX >= 0, "Block X should be positive");
            assertTrue(village.blockZ >= 0, "Block Z should be positive");
            assertTrue(village.chunkX >= 0, "Chunk X should be positive");
            assertTrue(village.chunkZ >= 0, "Chunk Z should be positive");
        }
    }

    @Test
    void findVillagesNearReturnsNearbyVillages() {
        VillageFinderService service = new VillageFinderService();

        // Search 50 chunks around origin
        List<VillageFinderService.VillageLocation> villages = 
            service.findVillagesNear(2603171484307346941L, 0, 0, 50);

        assertNotNull(villages);
        assertTrue(villages.size() < 1024, "Village count should stay bounded for normal radius");

        for (VillageFinderService.VillageLocation village : villages) {
            assertNotNull(village);
            long dx = village.chunkX;
            long dz = village.chunkZ;
            assertTrue((dx * dx) + (dz * dz) <= 50L * 50L, "Village should be inside requested radius");
        }
    }
}
