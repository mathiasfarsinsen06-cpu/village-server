package com.villagelocator.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VillageLocatorService {

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int searchRadius) {
        List<Map<String, Integer>> villages = new ArrayList<>();
        
        try {
            // Use cubiomes-java for accurate village finding
            // For now, we'll use a simple algorithm that matches Minecraft's structure generation
            
            final long VILLAGE_SALT = 10387312L;
            final int REGION_SIZE = 512; // 32 chunks * 16 blocks per chunk
            final int CHUNK_SPACING = 32;
            
            int regionStartX = (centerX - searchRadius) / REGION_SIZE;
            int regionStartZ = (centerZ - searchRadius) / REGION_SIZE;
            int regionEndX = (centerX + searchRadius) / REGION_SIZE + 1;
            int regionEndZ = (centerZ + searchRadius) / REGION_SIZE + 1;
            
            for (int regionX = regionStartX; regionX <= regionEndX; regionX++) {
                for (int regionZ = regionStartZ; regionZ <= regionEndZ; regionZ++) {
                    // Calculate structure seed for this region
                    long structureSeed = getStructureSeed(seed, regionX, regionZ, VILLAGE_SALT);
                    
                    // Use Random to get offset within region
                    Random rand = new Random(structureSeed);
                    int offsetX = rand.nextInt(CHUNK_SPACING - 8); // 8 is separation
                    int offsetZ = rand.nextInt(CHUNK_SPACING - 8);
                    
                    // Convert to block coordinates
                    int chunkX = regionX * CHUNK_SPACING + offsetX;
                    int chunkZ = regionZ * CHUNK_SPACING + offsetZ;
                    int blockX = chunkX * 16 + 8;
                    int blockZ = chunkZ * 16 + 8;
                    
                    // Distance check
                    int distSq = (blockX - centerX) * (blockX - centerX) + 
                                 (blockZ - centerZ) * (blockZ - centerZ);
                    
                    if (distSq < searchRadius * searchRadius) {
                        Map<String, Integer> village = new HashMap<>();
                        village.put("x", blockX);
                        village.put("z", blockZ);
                        villages.add(village);
                    }
                }
            }
            
            // Sort by distance
            villages.sort((a, b) -> {
                int distA = (a.get("x") - centerX) * (a.get("x") - centerX) + 
                           (a.get("z") - centerZ) * (a.get("z") - centerZ);
                int distB = (b.get("x") - centerX) * (b.get("x") - centerX) + 
                           (b.get("z") - centerZ) * (b.get("z") - centerZ);
                return Integer.compare(distA, distB);
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
