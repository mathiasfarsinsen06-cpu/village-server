package com.villagelocator.service;

import com.villagelocator.algorithm.VillageAlgorithm;
import com.villagelocator.biome.MinecraftBiomeOracle;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VillageFinderService {

    private static final long VILLAGE_SALT = 10387312L;
    private static final int VILLAGE_SPACING = 32;
    private static final int VILLAGE_SEPARATION = 8;
    private static final long REGION_SEED_X_MULTIPLIER = 341873128712L;
    private static final long REGION_SEED_Z_MULTIPLIER = 132897987541L;
    private static final int REGION_OFFSET_BOUND = VILLAGE_SPACING - VILLAGE_SEPARATION;

    public VillageFinderService() {
    }

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int radius) {
        List<Map<String, Integer>> villages = new ArrayList<>();

        // Build the AMIDST-style village algorithm with biome validation for this seed.
        VillageAlgorithm villageAlgorithm = new VillageAlgorithm(new MinecraftBiomeOracle(seed));

        int centerChunkX = centerX >> 4;
        int centerChunkZ = centerZ >> 4;
        int searchRadiusChunks = (radius + 15) >> 4;

        int minChunkX = centerChunkX - searchRadiusChunks;
        int maxChunkX = centerChunkX + searchRadiusChunks;
        int minChunkZ = centerChunkZ - searchRadiusChunks;
        int maxChunkZ = centerChunkZ + searchRadiusChunks;

        int minGridX = Math.floorDiv(minChunkX, VILLAGE_SPACING);
        int maxGridX = Math.floorDiv(maxChunkX, VILLAGE_SPACING);
        int minGridZ = Math.floorDiv(minChunkZ, VILLAGE_SPACING);
        int maxGridZ = Math.floorDiv(maxChunkZ, VILLAGE_SPACING);

        for (int gridX = minGridX; gridX <= maxGridX; gridX++) {
            for (int gridZ = minGridZ; gridZ <= maxGridZ; gridZ++) {
                int[] candidate = getCandidateChunkInRegion(seed, gridX, gridZ);
                int candidateChunkX = candidate[0];
                int candidateChunkZ = candidate[1];

                if (candidateChunkX >= minChunkX
                    && candidateChunkX <= maxChunkX
                    && candidateChunkZ >= minChunkZ
                    && candidateChunkZ <= maxChunkZ
                    && isWithinRadius(candidateChunkX, candidateChunkZ, centerX, centerZ, radius)
                    && villageAlgorithm.isValidLocation(candidateChunkX, candidateChunkZ)) {
                    Map<String, Integer> village = new HashMap<>();
                    village.put("x", candidateChunkX * 16 + 8);
                    village.put("z", candidateChunkZ * 16 + 8);
                    villages.add(village);
                }
            }
        }

        villages.sort(Comparator.comparingLong(v -> distanceSquared(v.get("x"), v.get("z"), centerX, centerZ)));
        return villages;
    }

    private int[] getCandidateChunkInRegion(long worldSeed, int gridX, int gridZ) {
        long regionSeed = worldSeed
            + (long) gridX * REGION_SEED_X_MULTIPLIER
            + (long) gridZ * REGION_SEED_Z_MULTIPLIER
            + VILLAGE_SALT;
        Random random = new Random(regionSeed);
        int offsetX = random.nextInt(REGION_OFFSET_BOUND);
        int offsetZ = random.nextInt(REGION_OFFSET_BOUND);
        return new int[] {
            gridX * VILLAGE_SPACING + offsetX,
            gridZ * VILLAGE_SPACING + offsetZ
        };
    }

    private boolean isWithinRadius(int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        int villageX = chunkX * 16 + 8;
        int villageZ = chunkZ * 16 + 8;
        return distanceSquared(villageX, villageZ, centerX, centerZ) <= (long) radius * radius;
    }

    private long distanceSquared(int x1, int z1, int x2, int z2) {
        long dx = (long) x1 - x2;
        long dz = (long) z1 - z2;
        return dx * dx + dz * dz;
    }
}
