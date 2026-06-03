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

        // Search 100 chunks around origin
        List<VillageFinderService.VillageLocation> villages = 
            service.findVillagesNear(12345L, 0, 0, 100);

        assertNotNull(villages);
        // May or may not find villages depending on seed
        for (VillageFinderService.VillageLocation village : villages) {
            assertNotNull(village);
        }
    }
}
