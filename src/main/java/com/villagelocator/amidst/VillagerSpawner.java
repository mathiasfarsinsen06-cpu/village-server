package com.villagelocator.amidst;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Minecraft 1.21.4 Villager spawner.
 * Simulates how many villagers spawn in a village based on seed and location.
 * 
 * In Minecraft:
 * - Villages spawn 1-10 villagers depending on buildings available
 * - Each profession is determined by job site block
 * - Villagers spread out around the village center
 */
public class VillagerSpawner {
    
    // Villager professions in Minecraft 1.21.4
    private static final String[] PROFESSIONS = {
        "farmer",
        "librarian",
        "cleric",
        "armorer",
        "butcher",
        "cartographer",
        "fisherman",
        "shepherd",
        "fletcher",
        "toolsmith"
    };
    
    /**
     * Generate villagers for a specific village location.
     * Uses seed and coordinates to deterministically spawn villagers.
     */
    public List<Villager> generateVillagersForChunk(long worldSeed, int chunkX, int chunkZ) {
        List<Villager> villagers = new ArrayList<>();
        
        // Create a seed specific to this chunk
        long chunkSeed = worldSeed;
        chunkSeed ^= ((long) chunkX * 73856093L);
        chunkSeed ^= ((long) chunkZ * 19349663L);
        
        JavaRandom random = new JavaRandom(chunkSeed);
        
        // Villages typically have 3-8 villagers on average
        // Use seed to determine exact count
        int villagerCount = 3 + random.nextInt(6); // 3-8 villagers
        
        System.out.println("[VILLAGER SPAWN] Chunk(" + chunkX + "," + chunkZ + ") spawning " + villagerCount + " villagers");
        
        // Generate each villager
        for (int i = 0; i < villagerCount; i++) {
            // Random profession
            String profession = PROFESSIONS[random.nextInt(PROFESSIONS.length)];
            
            // Random position within village (±128 blocks from center)
            int offsetX = random.nextInt(256) - 128;
            int offsetZ = random.nextInt(256) - 128;
            
            int blockX = (chunkX * 16 + 8) + offsetX;
            int blockZ = (chunkZ * 16 + 8) + offsetZ;
            
            // Generate unique name
            String name = generateVillagerName(random, i);
            
            // Random experience level (affects trading prices)
            int level = 1 + random.nextInt(5); // 1-5 (apprentice to master)
            
            villagers.add(new Villager(
                name,
                profession,
                blockX,
                blockZ,
                level
            ));
            
            System.out.println("[VILLAGER] " + name + " (" + profession + ") at " + blockX + "," + blockZ);
        }
        
        return villagers;
    }
    
    /**
     * Generate a unique villager name based on seed and index.
     */
    private String generateVillagerName(JavaRandom random, int index) {
        String[] firstNames = {"Bob", "Alice", "Charlie", "Diana", "Eve", "Frank", "Grace", "Henry"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Wilson"};
        
        String first = firstNames[random.nextInt(firstNames.length)];
        String last = lastNames[random.nextInt(lastNames.length)];
        
        return first + " " + last;
    }
    
    /**
     * Represents a single villager in a village.
     */
    public static class Villager {
        public final String name;
        public final String profession;
        public final int blockX;
        public final int blockZ;
        public final int level;  // 1-5 (apprentice to master)
        
        public Villager(String name, String profession, int blockX, int blockZ, int level) {
            this.name = name;
            this.profession = profession;
            this.blockX = blockX;
            this.blockZ = blockZ;
            this.level = level;
        }
        
        @Override
        public String toString() {
            return String.format("Villager(%s | %s | Level %d | %d,%d)", name, profession, level, blockX, blockZ);
        }
    }
}
