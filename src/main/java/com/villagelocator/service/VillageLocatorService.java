package com.villagelocator.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VillageLocatorService {

    // Village structure generation constants (Minecraft 1.16+)
    private static final long VILLAGE_SALT = 10387312L;
    private static final int REGION_SIZE = 32; // chunks
    private static final int REGION_SPACING = 34; // structure spacing in chunks

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int searchRadius) {
        List<Map<String, Integer>> villages = new ArrayList<>();
        
        try {
            // Convert block coordinates to chunk coordinates
            int centerChunkX = centerX >> 4;
            int centerChunkZ = centerZ >> 4;
            
            // Convert to region coordinates (32 chunks per region)
            int centerRegionX = centerChunkX >> 5;
            int centerRegionZ = centerChunkZ >> 5;
            
            // Search radius in regions
            int searchRadiusRegions = (searchRadius >> 9) + 2; // 9 = log2(512)
            
            for (int regionX = centerRegionX - searchRadiusRegions; regionX <= centerRegionX + searchRadiusRegions; regionX++) {
                for (int regionZ = centerRegionZ - searchRadiusRegions; regionZ <= centerRegionZ + searchRadiusRegions; regionZ++) {
                    
                    // Calculate structure seed for this region
                    long structureSeed = getStructureSeed(seed, regionX, regionZ, VILLAGE_SALT);
                    Random rand = new Random(structureSeed);
                    
                    // Get random offset within the region
                    int offsetX = rand.nextInt(REGION_SPACING - 8);
                    int offsetZ = rand.nextInt(REGION_SPACING - 8);
                    
                    // Calculate chunk coordinates
                    int chunkX = regionX * REGION_SPACING + offsetX;
                    int chunkZ = regionZ * REGION_SPACING + offsetZ;
                    
                    // Convert to block coordinates (chunk center)
                    int blockX = (chunkX << 4) + 8;
                    int blockZ = (chunkZ << 4) + 8;
                    
                    // Distance check
                    int dx = blockX - centerX;
                    int dz = blockZ - centerZ;
                    long distSq = (long)dx * dx + (long)dz * dz;
                    
                    if (distSq < (long)searchRadius * searchRadius) {
                        Map<String, Integer> village = new HashMap<>();
                        village.put("x", blockX);
                        village.put("z", blockZ);
                        villages.add(village);
                    }
                }
            }
            
            // Sort by distance from center
            villages.sort((a, b) -> {
                int dx1 = a.get("x") - centerX;
                int dz1 = a.get("z") - centerZ;
                int dx2 = b.get("x") - centerX;
                int dz2 = b.get("z") - centerZ;
                
                long dist1 = (long)dx1 * dx1 + (long)dz1 * dz1;
                long dist2 = (long)dx2 * dx2 + (long)dz2 * dz2;
                
                return Long.compare(dist1, dist2);
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return villages;
    }
    
    // Replicates Minecraft's structure seed calculation
    private long getStructureSeed(long worldSeed, int regionX, int regionZ, long salt) {
        long seed = worldSeed;
        seed += (long)regionX * 341873128712L;
        seed += (long)regionZ * 132897987541L;
        seed += salt;
        return seed;
    }
}
