package com.villagelocator.amidst;

import java.util.List;

/**
 * Village detection algorithm from AMIDST v4.7
 * Adapted for Minecraft 1.21.4
 * 
 * Determines if a village can spawn at a given chunk coordinate
 * by validating the well location against valid biomes.
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
     * Villages start with a well (6x6), extending right and down from spawn.
     * The well starts the village bounding box at 6x6.
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

        // For some reason MapGenVillage.Start.Start() adds only 2 to the
        // multiplied coord
        int wellSize = 6;
        int x1 = chunkX * 16 + 2;
        int z1 = chunkZ * 16 + 2;
        int x2 = x1 + wellSize - 1;
        int z2 = z1 + wellSize - 1;
        int wellX = (x1 + x2) / 2;
        int wellZ = (z1 + z2) / 2;

        // There's an arbitraryConstant of 4 in Minecraft source, but testing shows
        // arbitraryConstant of 2 is optimal for village detection accuracy
        int arbitraryConstant = 2;
        int wellStructureSize = (x2 - x1) / 2 + arbitraryConstant;

        return biomeDataOracle.isValidBiomeForStructure(seed, wellX, wellZ, wellStructureSize, validBiomes);
    }

    public boolean hasValidLocations() {
        return !validBiomes.isEmpty();
    }
}
