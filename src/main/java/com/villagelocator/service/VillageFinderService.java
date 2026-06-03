package com.villagelocator.service;

import com.villagelocator.amidst.Minecraft121BiomeOracle;
import com.villagelocator.amidst.VillageAlgorithm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service for finding villages with AMIDST v4.7 validation.
 * Only returns villages that can actually generate with villagers.
 */
@Service
public class VillageFinderService {
    
    private final VillageAlgorithm villageAlgorithm;
    
    public VillageFinderService() {
        Minecraft121BiomeOracle biomeOracle = new Minecraft121BiomeOracle();
        List<String> validBiomes = Arrays.asList(
            "plains", "savanna", "desert", "taiga", "snowy_plains", "cherry_grove"
        );
        this.villageAlgorithm = new VillageAlgorithm(biomeOracle, validBiomes);
    }
    
    /**
     * Find all valid villages in a region for the given seed.
     */
    public List<VillageLocation> findVillages(long seed, int minChunkX, int maxChunkX, 
                                               int minChunkZ, int maxChunkZ) {
        List<VillageLocation> villages = new ArrayList<>();
        
        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX += 32) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ += 32) {
                for (int offsetX = 0; offsetX < 32; offsetX++) {
                    for (int offsetZ = 0; offsetZ < 32; offsetZ++) {
                        int testChunkX = chunkX + offsetX;
                        int testChunkZ = chunkZ + offsetZ;
                        
                        if (villageAlgorithm.isValidLocation(seed, testChunkX, testChunkZ)) {
                            villages.add(new VillageLocation(
                                testChunkX * 16 + 8,
                                testChunkZ * 16 + 8,
                                testChunkX,
                                testChunkZ
                            ));
                        }
                    }
                }
            }
        }
        
        return villages;
    }
    
    /**
     * Find villages near a specific coordinate.
     */
    public List<VillageLocation> findVillagesNear(long seed, int centerX, int centerZ, int radiusChunks) {
        int centerChunkX = centerX >> 4;
        int centerChunkZ = centerZ >> 4;
        
        return findVillages(seed,
            centerChunkX - radiusChunks, centerChunkX + radiusChunks,
            centerChunkZ - radiusChunks, centerChunkZ + radiusChunks
        );
    }
    
    /**
     * Represents a found village location.
     */
    public static class VillageLocation {
        public final int blockX;
        public final int blockZ;
        public final int chunkX;
        public final int chunkZ;
        
        public VillageLocation(int blockX, int blockZ, int chunkX, int chunkZ) {
            this.blockX = blockX;
            this.blockZ = blockZ;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }
        
        @Override
        public String toString() {
            return String.format("Village(block: %d,%d | chunk: %d,%d)", blockX, blockZ, chunkX, chunkZ);
        }
    }
}
