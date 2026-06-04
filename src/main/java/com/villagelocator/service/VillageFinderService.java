package com.villagelocator.service;

import com.villagelocator.amidst.Minecraft121BiomeOracle;
import com.villagelocator.amidst.VillageStructureFinder;
import com.villagelocator.amidst.VillageStructureFinder.VillageChunk;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for finding villages using Minecraft 1.21.4 structure generation.
 *
 * This service now ONLY returns village locations.
 * Villager scanning is intentionally left to the client/mod.
 */
@Service
public class VillageFinderService {
    
    private final VillageStructureFinder villageStructureFinder;
    private final Minecraft121BiomeOracle biomeOracle;
    
    public VillageFinderService() {
        this.villageStructureFinder = new VillageStructureFinder();
        this.biomeOracle = new Minecraft121BiomeOracle();
    }
    
    /**
     * Find all valid villages in a region for the given seed.
     * Uses Minecraft's structure generation algorithm.
     */
    public List<VillageLocation> findVillages(long seed, int minChunkX, int maxChunkX,
                                              int minChunkZ, int maxChunkZ) {
        List<VillageLocation> villages = new ArrayList<>();
        
        System.out.println("[VILLAGE SERVICE] Finding villages for seed=" + seed +
                " region X:" + minChunkX + "-" + maxChunkX + " Z:" + minChunkZ + "-" + maxChunkZ);
        
        List<VillageChunk> structureLocations = villageStructureFinder.findVillages(
                seed, minChunkX, maxChunkX, minChunkZ, maxChunkZ
        );
        
        System.out.println("[VILLAGE SERVICE] Found " + structureLocations.size() + " potential village chunks");
        
        for (VillageChunk villageChunk : structureLocations) {
            if (biomeOracle.isValidBiomeForVillage(seed, villageChunk.blockX, villageChunk.blockZ)) {
                villages.add(new VillageLocation(
                        villageChunk.blockX,
                        villageChunk.blockZ,
                        villageChunk.chunkX,
                        villageChunk.chunkZ
                ));
                System.out.println("[VILLAGE VALID] Biome check passed for chunk " +
                        villageChunk.chunkX + "," + villageChunk.chunkZ);
            } else {
                System.out.println("[VILLAGE REJECTED] Biome check failed for chunk " +
                        villageChunk.chunkX + "," + villageChunk.chunkZ);
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
     * JSON-serializable village location.
     */
    public static class VillageLocation {
        @JsonProperty("blockX")
        public final int blockX;
        
        @JsonProperty("blockZ")
        public final int blockZ;
        
        @JsonProperty("chunkX")
        public final int chunkX;
        
        @JsonProperty("chunkZ")
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
