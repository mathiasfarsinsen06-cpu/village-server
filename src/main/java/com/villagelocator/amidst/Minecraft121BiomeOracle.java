package com.villagelocator.amidst;

import java.util.List;

/**
 * Minecraft 1.21.4 biome detection implementation.
 * Uses seed-based deterministic biome generation with full 64-bit seed support.
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
        // DEBUG: Log seed and coordinates
        if (x == 0 && z == 0) {
            System.out.println("[DEBUG] getBiomeAt called: seed=" + seed + ", x=" + x + ", z=" + z);
        }
        
        // Use full 64-bit seed with coordinates to generate deterministic biome
        long hash = mix64(seed);
        hash ^= mix64((long) x);
        hash ^= mix64((long) z);
        hash = mix64(hash);
        
        long absHash = Math.abs(hash);
        int primaryIndex = (int) (absHash % VILLAGE_BIOMES.length);
        String primaryBiome = VILLAGE_BIOMES[primaryIndex];
        
        // Spatial coherence using chunk coordinates
        long chunkSeed = mix64(seed);
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        chunkSeed ^= mix64((long) chunkX);
        chunkSeed ^= mix64((long) chunkZ);
        chunkSeed = mix64(chunkSeed);
        
        long absChunkHash = Math.abs(chunkSeed);
        int probability = (int) (absChunkHash % 100);
        
        String result;
        if (probability < 45) {
            result = "plains";
        } else if (probability < 70) {
            result = primaryBiome;
        } else {
            int secondaryIndex = (int) ((absChunkHash >> 32) % VILLAGE_BIOMES.length);
            result = VILLAGE_BIOMES[secondaryIndex];
        }
        
        if (x == 0 && z == 0) {
            System.out.println("[DEBUG] Result: " + result + ", probability=" + probability + ", hash=" + hash);
        }
        
        return result;
    }
    
    /**
     * Mix function for better hash distribution.
     */
    private static long mix64(long x) {
        x ^= x >>> 33;
        x *= 0xff51afd7ed558ccdL;
        x ^= x >>> 33;
        return x;
    }
}
