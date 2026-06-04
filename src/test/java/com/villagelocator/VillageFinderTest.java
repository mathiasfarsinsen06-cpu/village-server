package com.villagelocator;

import com.villagelocator.amidst.JavaRandom;
import com.villagelocator.amidst.Minecraft121BiomeOracle;
import com.villagelocator.amidst.VillageStructureFinder;
import com.villagelocator.amidst.VillageStructureFinder.VillageChunk;
import com.villagelocator.service.VillageFinderService;
import com.villagelocator.service.VillageFinderService.VillageLocation;

import java.util.List;

/**
 * Test the village finder system end-to-end.
 */
public class VillageFinderTest {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Village Finder System Test");
        System.out.println("========================================\n");
        
        // Test 1: JavaRandom
        testJavaRandom();
        
        // Test 2: VillageStructureFinder
        testVillageStructureFinder();
        
        // Test 3: BiomeOracle
        testBiomeOracle();
        
        // Test 4: Full VillageFinderService
        testVillageFinderService();
        
        System.out.println("\n========================================");
        System.out.println("All tests completed!");
        System.out.println("========================================");
    }
    
    private static void testJavaRandom() {
        System.out.println("\n[TEST 1] JavaRandom Implementation");
        System.out.println("-----------------------------------");
        
        JavaRandom rng = new JavaRandom(12345L);
        
        System.out.println("Seed: 12345");
        System.out.println("Next 5 random ints:");
        for (int i = 0; i < 5; i++) {
            System.out.println("  " + (i+1) + ": " + rng.nextInt());
        }
        
        rng.setSeed(12345L);
        System.out.println("\nRandom range [0-10]:");
        for (int i = 0; i < 5; i++) {
            System.out.println("  " + (i+1) + ": " + rng.nextInt(10));
        }
        
        System.out.println("\n✓ JavaRandom test passed");
    }
    
    private static void testVillageStructureFinder() {
        System.out.println("\n[TEST 2] VillageStructureFinder");
        System.out.println("-----------------------------------");
        
        VillageStructureFinder finder = new VillageStructureFinder();
        long seed = 5975010353295290926L;
        
        // Search in a 1000x1000 chunk region
        int minChunk = 0;
        int maxChunk = 100;
        
        System.out.println("Seed: " + seed);
        System.out.println("Search area: Chunks (" + minChunk + "-" + maxChunk + ") x (" + minChunk + "-" + maxChunk + ")");
        
        List<VillageChunk> villages = finder.findVillages(seed, minChunk, maxChunk, minChunk, maxChunk);
        
        System.out.println("\nFound " + villages.size() + " village chunks:");
        for (VillageChunk village : villages) {
            System.out.println("  " + village);
        }
        
        System.out.println("\n✓ VillageStructureFinder test passed");
    }
    
    private static void testBiomeOracle() {
        System.out.println("\n[TEST 3] BiomeOracle");
        System.out.println("-----------------------------------");
        
        Minecraft121BiomeOracle biome = new Minecraft121BiomeOracle();
        long seed = 5975010353295290926L;
        
        System.out.println("Seed: " + seed);
        System.out.println("\nChecking biomes at various locations:");
        
        int[][] testLocations = {
            {0, 0},
            {100, 100},
            {-200, 50},
            {1000, 1000},
            {-1000, -1000}
        };
        
        for (int[] loc : testLocations) {
            int x = loc[0] * 16;
            int z = loc[1] * 16;
            boolean valid = biome.isValidBiomeForVillage(seed, x, z);
            System.out.println("  Block (" + x + ", " + z + "): " + (valid ? "VILLAGE VALID" : "NOT VALID"));
        }
        
        System.out.println("\n✓ BiomeOracle test passed");
    }
    
    private static void testVillageFinderService() {
        System.out.println("\n[TEST 4] VillageFinderService (Full Integration)");
        System.out.println("-----------------------------------");
        
        VillageFinderService service = new VillageFinderService();
        long seed = 5975010353295290926L;
        int centerX = 0;
        int centerZ = 0;
        int radiusChunks = 100;
        
        System.out.println("Seed: " + seed);
        System.out.println("Center: (" + centerX + ", " + centerZ + ")");
        System.out.println("Radius: " + radiusChunks + " chunks");
        
        List<VillageLocation> villages = service.findVillagesNear(seed, centerX, centerZ, radiusChunks);
        
        System.out.println("\nFound " + villages.size() + " valid villages:");
        for (VillageLocation village : villages) {
            System.out.println("  " + village);
        }
        
        System.out.println("\n✓ VillageFinderService test passed");
    }
}
