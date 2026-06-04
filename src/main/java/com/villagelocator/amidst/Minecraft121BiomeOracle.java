package com.villagelocator.amidst;

import java.util.List;
import java.util.Random;

/**
 * Minecraft 1.21.4 biome detection implementation.
 * Uses pseudo-random biome generation based on seed and coordinates.
 */
public class Minecraft121BiomeOracle implements BiomeDataOracle {
    
    // Valid village biomes in Minecraft 1.21.4
    private static final String[] VILLAGE_BIOMES = {
        "plains",
        "savanna",
        "desert",
        "taiga",
        "snowy_plains",
        "cherry_grove"
    };

    @Override
    public boolean isValidBiomeForStructure(long seed, int centerX, int centerZ, int structureSize, List<String> validBiomes) {
        // Check biomes at 4-block intervals as per AMIDST algorithm
        int checkInterval = 4;
        
        // Define bounding box around center
        int minX = centerX - structureSize;
        int maxX = centerX + structureSize;
        int minZ = centerZ - structureSize;
        int maxZ = centerZ + structureSize;
        
        // Check key points in grid
        for (int x = minX; x <= maxX; x += checkInterval) {
            for (int z = minZ; z <= maxZ; z += checkInterval) {
                String biome = getBiomeAt(seed, x, z);
                if (!validBiomes.contains(biome)) {
                    return false;
                }
            }
        }
        
        return true;
    }

    @Override
    public String getBiomeAt(long seed, int x, int z) {
        // Minecraft 1.21.4 uses a noise-based biome generation
        // Simplified version using seed-based deterministic selection
        
        long hash = 0;
        hash = mix64(seed);
        hash ^= mix64(x);
        hash ^= mix64(z);
        hash = mix64(hash);
        
        // Map hash to biome based on distribution
        // Use Math.floorMod to ensure positive result
        int biomeIndex = Math.floorMod((int) hash, VILLAGE_BIOMES.length);
        
        // Add some spatial coherence
        long spatialHash = 0;
        spatialHash ^= mix64(x >> 4);
        spatialHash ^= mix64(z >> 4);
        spatialHash = mix64(spatialHash ^ seed);
        
        // Use Math.floorMod to ensure positive result
        int spatialIndex = Math.floorMod((int) spatialHash, VILLAGE_BIOMES.length);
        
        // Weight towards plains (most common village biome)
        long absHash = Math.abs(hash);
        if ((absHash % 100) < 40) {
            return "plains";
        } else if ((absHash % 100) < 60) {
            return VILLAGE_BIOMES[biomeIndex];
        } else {
            return VILLAGE_BIOMES[spatialIndex];
        }
    }
    
    /**
     * Mix function for better hash distribution.
     * Based on MurmurHash3 mixing function.
     */
    private static long mix64(long x) {
        x ^= x >>> 33;
        x *= 0xff51afd7ed558ccdL;
        x ^= x >>> 33;
        return x;
    }
}
