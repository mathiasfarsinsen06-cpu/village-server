package com.villagelocator.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class VillageFinderService {

    private static final long VILLAGE_SALT = 10387312L;
    private static final int VILLAGE_SPACING = 32;
    private static final int VILLAGE_SEPARATION = 8;
    private static final long MAGIC_NUMBER_1 = 341873128712L;
    private static final long MAGIC_NUMBER_2 = 132897987541L;

    private static final int WELL_OFFSET = 2;
    private static final int WELL_SIZE = 6;
    private static final int ARBITRARY_CONSTANT = 2;

    private static final Set<String> VALID_VILLAGE_BIOMES = Set.of(
        "savanna",
        "plains",
        "desert",
        "taiga",
        "snowy_plains"
    );

    private final BiomeResolver biomeResolver;

    public VillageFinderService() {
        this((seed, x, z) -> "plains");
    }

    VillageFinderService(BiomeResolver biomeResolver) {
        this.biomeResolver = biomeResolver;
    }

    public List<Map<String, Integer>> findVillages(long seed, int centerX, int centerZ, int radius) {
        List<Map<String, Integer>> villages = new ArrayList<>();

        int centerChunkX = centerX >> 4;
        int centerChunkZ = centerZ >> 4;
        int searchRadiusChunks = (radius + 15) >> 4;

        int minChunkX = centerChunkX - searchRadiusChunks;
        int maxChunkX = centerChunkX + searchRadiusChunks;
        int minChunkZ = centerChunkZ - searchRadiusChunks;
        int maxChunkZ = centerChunkZ + searchRadiusChunks;

        int minRegionX = Math.floorDiv(minChunkX, VILLAGE_SPACING);
        int maxRegionX = Math.floorDiv(maxChunkX, VILLAGE_SPACING);
        int minRegionZ = Math.floorDiv(minChunkZ, VILLAGE_SPACING);
        int maxRegionZ = Math.floorDiv(maxChunkZ, VILLAGE_SPACING);

        for (int regionX = minRegionX; regionX <= maxRegionX; regionX++) {
            for (int regionZ = minRegionZ; regionZ <= maxRegionZ; regionZ++) {
                int[] candidate = getCandidateChunkInRegion(seed, regionX, regionZ);
                int candidateChunkX = candidate[0];
                int candidateChunkZ = candidate[1];

                if (candidateChunkX >= minChunkX
                    && candidateChunkX <= maxChunkX
                    && candidateChunkZ >= minChunkZ
                    && candidateChunkZ <= maxChunkZ
                    && isValidVillageLocation(seed, candidateChunkX, candidateChunkZ, centerX, centerZ, radius)) {
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

    private int[] getCandidateChunkInRegion(long worldSeed, int regionX, int regionZ) {
        Random random = new Random(getRegionSeed(worldSeed, regionX, regionZ));
        int offsetX = random.nextInt(VILLAGE_SPACING - VILLAGE_SEPARATION);
        int offsetZ = random.nextInt(VILLAGE_SPACING - VILLAGE_SEPARATION);
        return new int[] {
            regionX * VILLAGE_SPACING + offsetX,
            regionZ * VILLAGE_SPACING + offsetZ
        };
    }

    private long getRegionSeed(long worldSeed, int regionX, int regionZ) {
        return (long) regionX * MAGIC_NUMBER_1 + (long) regionZ * MAGIC_NUMBER_2 + worldSeed + VILLAGE_SALT;
    }

    private boolean isValidVillageLocation(long seed, int chunkX, int chunkZ, int centerX, int centerZ, int radius) {
        int villageX = chunkX * 16 + 8;
        int villageZ = chunkZ * 16 + 8;
        if (distanceSquared(villageX, villageZ, centerX, centerZ) > (long) radius * radius) {
            return false;
        }
        return isValidBiomeForStructure(seed, villageX, villageZ, 0) && isValidWellLocation(seed, chunkX, chunkZ);
    }

    private boolean isValidWellLocation(long seed, int chunkX, int chunkZ) {
        int x1 = chunkX * 16 + WELL_OFFSET;
        int z1 = chunkZ * 16 + WELL_OFFSET;
        int x2 = x1 + WELL_SIZE - 1;
        int z2 = z1 + WELL_SIZE - 1;

        int wellX = (x1 + x2) / 2;
        int wellZ = (z1 + z2) / 2;
        int wellStructureSize = (x2 - x1) / 2 + ARBITRARY_CONSTANT;

        return isValidBiomeForStructure(seed, wellX, wellZ, wellStructureSize);
    }

    private boolean isValidBiomeForStructure(long seed, int centerX, int centerZ, int structureSize) {
        int minX = centerX - structureSize * 4;
        int maxX = centerX + structureSize * 4;
        int minZ = centerZ - structureSize * 4;
        int maxZ = centerZ + structureSize * 4;

        for (int x = minX; x <= maxX; x += 4) {
            for (int z = minZ; z <= maxZ; z += 4) {
                String biome = biomeResolver.resolve(seed, x, z);
                if (biome == null || !VALID_VILLAGE_BIOMES.contains(biome.toLowerCase(Locale.ROOT))) {
                    return false;
                }
            }
        }

        return true;
    }

    private long distanceSquared(int x1, int z1, int x2, int z2) {
        long dx = (long) x1 - x2;
        long dz = (long) z1 - z2;
        return dx * dx + dz * dz;
    }

    @FunctionalInterface
    interface BiomeResolver {
        String resolve(long seed, int blockX, int blockZ);
    }
}
