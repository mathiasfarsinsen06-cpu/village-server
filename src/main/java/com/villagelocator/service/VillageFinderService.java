package com.villagelocator.service;

import com.villagelocator.amidst.Minecraft121BiomeOracle;
import com.villagelocator.amidst.VillageStructureFinder;
import com.villagelocator.amidst.VillageStructureFinder.VillageChunk;
import com.villagelocator.amidst.VillagerSpawner;
import com.villagelocator.amidst.VillagerSpawner.Villager;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for finding villages using Minecraft 1.21.4's EXACT algorithm.
 * Now uses real village structure finder instead of fake algorithm.
 */
@Service
public class VillageFinderService {
    
    private final VillageStructureFinder villageStructureFinder;
    private final Minecraft121BiomeOracle biomeOracle;
    private final VillagerSpawner villagerSpawner;
    
    public VillageFinderService() {
        this.villageStructureFinder = new VillageStructureFinder();
        this.biomeOracle = new Minecraft121BiomeOracle();
        this.villagerSpawner = new VillagerSpawner();
    }
    
    /**
     * Find all valid villages in a region for the given seed.
     * Uses Minecraft's real structure generation algorithm.
     * Also spawns villagers for each village.
     */
    public List<VillageLocation> findVillages(long seed, int minChunkX, int maxChunkX, 
                                               int minChunkZ, int maxChunkZ) {
        List<VillageLocation> villages = new ArrayList<>();
        
        System.out.println("[VILLAGE SERVICE] Finding villages for seed=" + seed + 
                         " region X:" + minChunkX + "-" + maxChunkX + " Z:" + minChunkZ + "-" + maxChunkZ);
        
        // Use real village structure finder
        List<VillageChunk> structureLocations = villageStructureFinder.findVillages(
            seed, minChunkX, maxChunkX, minChunkZ, maxChunkZ
        );
        
        System.out.println("[VILLAGE SERVICE] Found " + structureLocations.size() + " potential village chunks");
        
        // Validate each village location with biome check and spawn villagers
        for (VillageChunk villageChunk : structureLocations) {
            // Check if biome allows villages at this location
            if (biomeOracle.isValidBiomeForVillage(seed, villageChunk.blockX, villageChunk.blockZ)) {
                // Generate villagers for this village
                List<Villager> villagers = villagerSpawner.generateVillagersForChunk(seed, villageChunk.chunkX, villageChunk.chunkZ);
                
                villages.add(new VillageLocation(
                    villageChunk.blockX,
                    villageChunk.blockZ,
                    villageChunk.chunkX,
                    villageChunk.chunkZ,
                    villagers
                ));
                System.out.println("[VILLAGE VALID] Biome check passed for chunk " + 
                                 villageChunk.chunkX + "," + villageChunk.chunkZ + 
                                 " with " + villagers.size() + " villagers");
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
     * Represents a found village location with villagers and proper JSON serialization.
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
        
        @JsonProperty("villagers")
        public final List<VillagerInfo> villagers;
        
        public VillageLocation(int blockX, int blockZ, int chunkX, int chunkZ, List<Villager> villagerList) {
            this.blockX = blockX;
            this.blockZ = blockZ;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            
            // Convert Villager objects to VillagerInfo for JSON
            this.villagers = new ArrayList<>();
            for (Villager v : villagerList) {
                this.villagers.add(new VillagerInfo(v.name, v.profession, v.blockX, v.blockZ, v.level));
            }
        }
        
        @Override
        public String toString() {
            return String.format("Village(block: %d,%d | chunk: %d,%d | villagers: %d)", 
                blockX, blockZ, chunkX, chunkZ, villagers.size());
        }
    }
    
    /**
     * JSON-serializable villager information.
     */
    public static class VillagerInfo {
        @JsonProperty("name")
        public final String name;
        
        @JsonProperty("profession")
        public final String profession;
        
        @JsonProperty("blockX")
        public final int blockX;
        
        @JsonProperty("blockZ")
        public final int blockZ;
        
        @JsonProperty("level")
        public final int level;
        
        public VillagerInfo(String name, String profession, int blockX, int blockZ, int level) {
            this.name = name;
            this.profession = profession;
            this.blockX = blockX;
            this.blockZ = blockZ;
            this.level = level;
        }
        
        @Override
        public String toString() {
            return String.format("Villager(%s | %s | Level %d)", name, profession, level);
        }
    }
}
