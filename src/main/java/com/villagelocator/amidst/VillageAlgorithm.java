package com.villagelocator.amidst;

import java.util.List;

/**
 * Village detection algorithm from AMIDST v4.7
 * Adapted for Minecraft 1.21.4
 * 
 * Determines if a village can spawn at a given chunk coordinate
 * by validating the well location against valid biomes.
 * Villages spawn in a grid pattern determined by the world seed.
 */
public class VillageAlgorithm {
    private final BiomeDataOracle biomeDataOracle;
    private final List<String> validBiomes;

    public VillageAlgorithm(BiomeDataOracle biomeDataOracle, List<String> validBiomes) {
        this.biomeDataOracle = biomeDataOracle;
        this.validBiomes = validBiomes;
    }

    /**
     * Check if a village can spawn at the given chunk coordinates.
     * 
     * In Minecraft, villages generate in a grid pattern. Each 32x32 chunk region
     * has a potential village spawn point determined by the seed. This method
     * checks if the given chunk is where a village attempt would occur for this seed.
     * 
     * @param chunkX chunk X coordinate
     * @param chunkZ chunk Z coordinate
     * @param seed world seed
     * @return true if village can spawn here
     */
    public boolean isValidLocation(long seed, int chunkX, int chunkZ) {
        if (validBiomes.isEmpty()) {
            return false;
        }

        // Villages attempt to spawn in a grid of 32x32 chunk regions
        // The exact spawn point within the region depends on the seed
        int regionX = chunkX >> 5;  // Divide by 32
        int regionZ = chunkZ >> 5;  // Divide by 32
        
        // Generate seed-based random offset within this region
        long regionSeed = seed;
        regionSeed ^= mix64((long) regionX);
        regionSeed ^= mix64((long) regionZ);
        regionSeed = mix64(regionSeed);
        
        // Get pseudo-random offsets (0-31) for where village spawns in this region
        int offsetX = Math.floorMod((int) regionSeed, 32);
        int offsetZ = Math.floorMod((int) (regionSeed >> 32), 32);
        
        // Calculate the actual chunk where village should spawn for this seed
        int villageChunkX = (regionX << 5) + offsetX;
        int villageChunkZ = (regionZ << 5) + offsetZ;
        
        // Only consider villages at their seed-determined chunk position
        if (chunkX != villageChunkX || chunkZ != villageChunkZ) {
            return false;
        }

        // Now validate the well location biomes
        int wellSize = 6;
        int x1 = chunkX * 16 + 2;
        int z1 = chunkZ * 16 + 2;
        int x2 = x1 + wellSize - 1;
        int z2 = z1 + wellSize - 1;
        int wellX = (x1 + x2) / 2;
        int wellZ = (z1 + z2) / 2;

        int arbitraryConstant = 2;
        int wellStructureSize = (x2 - x1) / 2 + arbitraryConstant;

        return biomeDataOracle.isValidBiomeForStructure(seed, wellX, wellZ, wellStructureSize, validBiomes);
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

    public boolean hasValidLocations() {
        return !validBiomes.isEmpty();
    }
}
