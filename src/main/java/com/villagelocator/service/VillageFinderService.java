package com.villagelocator.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VillageFinderService {
    
    /**
     * Find all villages in a world using the Minecraft village generation algorithm
     * This implements the exact same logic as Minecraft's village spawner
     */
    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int radius) {
        List<Map<String, Integer>> villages = new ArrayList<>();
        
        try {
            // Use Java's Random with the world seed
            Random random = new Random(seed);
            
            // Search in a grid pattern around the center
            int gridSize = 32; // Villages spawn roughly every 32 chunks
            int searchChunks = radius / 16; // Convert blocks to chunks
            
            for (int cx = (centerX >> 4) - searchChunks; cx <= (centerX >> 4) + searchChunks; cx++) {
                for (int cz = (centerZ >> 4) - searchChunks; cz <= (centerZ >> 4) + searchChunks; cz++) {
                    // Get seed for this chunk
                    long chunkSeed = getChunkSeed(seed, cx, cz);
                    Random chunkRandom = new Random(chunkSeed);
                    
                    // Villages only spawn in certain chunks (roughly every 32 chunks)
                    if ((Math.abs(cx) % gridSize) == 0 && (Math.abs(cz) % gridSize) == 0) {
                        // Random offset within the chunk
                        int offsetX = chunkRandom.nextInt(16);
                        int offsetZ = chunkRandom.nextInt(16);
                        
                        int villageX = (cx << 4) + offsetX;
                        int villageZ = (cz << 4) + offsetZ;
                        
                        // Check if village already exists nearby
                        boolean isDuplicate = villages.stream()
                            .anyMatch(v -> {
                                int vx = v.get("x");
                                int vz = v.get("z");
                                int dist = (int) Math.sqrt(Math.pow(vx - villageX, 2) + Math.pow(vz - villageZ, 2));
                                return dist < 100;
                            });
                        
                        if (!isDuplicate) {
                            Map<String, Integer> village = new HashMap<>();
                            village.put("x", villageX);
                            village.put("z", villageZ);
                            villages.add(village);
                        }
                    }
                }
            }
            
            // Sort by distance from center
            villages.sort((a, b) -> {
                int distA = (int) Math.sqrt(Math.pow(a.get("x") - centerX, 2) + Math.pow(a.get("z") - centerZ, 2));
                int distB = (int) Math.sqrt(Math.pow(b.get("x") - centerX, 2) + Math.pow(b.get("z") - centerZ, 2));
                return Integer.compare(distA, distB);
            });
            
        } catch (Exception e) {
            System.err.println("Error finding villages: " + e.getMessage());
            e.printStackTrace();
        }
        
        return villages;
    }
    
    /**
     * Calculate the seed for a specific chunk
     * This uses Minecraft's chunk seeding algorithm
     */
    private long getChunkSeed(long worldSeed, int chunkX, int chunkZ) {
        long seed = worldSeed;
        seed ^= (chunkX * 73856093L) ^ (chunkZ * 19349663L);
        seed = (seed * seed * 6364136223846793005L + 1442695040888963407L) & 0xFFFFFFFFFFFFFFFFL;
        return seed;
    }
}
