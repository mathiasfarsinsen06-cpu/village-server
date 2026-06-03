package com.villagelocator.algorithm;

import com.villagelocator.biome.BiomeDataOracle;

import java.util.Set;

/**
 * Validates whether a village can actually spawn at a given chunk position.
 *
 * <p>Ported from AMIDST's {@code VillageAlgorithm} class
 * (toolbox4minecraft/amidst, LocationChecker implementations).
 * The core insight is that a village's first structure — its well — occupies a
 * 6×6 bounding box beginning at chunk offset +2.  If the biomes under and
 * around that well are not all valid village biomes the village will not
 * generate, so checking just the well area is a very effective filter.</p>
 *
 * <p>Valid village biomes for Minecraft 1.21.4:
 * <ul>
 *   <li>plains</li>
 *   <li>savanna</li>
 *   <li>desert</li>
 *   <li>taiga</li>
 *   <li>snowy_plains</li>
 *   <li>cherry_grove (added in 1.21)</li>
 * </ul>
 * </p>
 */
public class VillageAlgorithm {

    /** Minecraft resource-location names for biomes that support villages. */
    public static final Set<String> VALID_VILLAGE_BIOMES = Set.of(
            "plains",
            "savanna",
            "desert",
            "taiga",
            "snowy_plains",
            "cherry_grove"
    );

    private final BiomeDataOracle biomeDataOracle;
    private final Set<String> validBiomes;

    public VillageAlgorithm(BiomeDataOracle biomeDataOracle) {
        this(biomeDataOracle, VALID_VILLAGE_BIOMES);
    }

    public VillageAlgorithm(BiomeDataOracle biomeDataOracle, Set<String> validBiomes) {
        this.biomeDataOracle = biomeDataOracle;
        this.validBiomes = validBiomes;
    }

    /**
     * Returns {@code true} if the biomes at the village well position are all
     * valid for village generation.
     *
     * <p>Algorithm (from AMIDST's {@code VillageAlgorithm.isValidLocation}):
     * <ol>
     *   <li>The village well is a 6×6 structure that starts at chunk offset +2
     *       (i.e. block {@code chunkX*16+2}, {@code chunkZ*16+2}).</li>
     *   <li>An {@code arbitraryConstant} of 2 extends the check by one block
     *       around the well, giving {@code wellStructureSize = (x2−x1)/2 + 2 = 4}.</li>
     *   <li>{@link BiomeDataOracle#isValidBiomeForStructure} scans the resulting
     *       region at quarter-resolution (4-block intervals) and returns
     *       {@code true} only if every sampled biome is in {@code validBiomes}.</li>
     * </ol>
     * </p>
     *
     * @param chunkX chunk X coordinate
     * @param chunkZ chunk Z coordinate
     * @return {@code true} if the well area is in a valid village biome
     */
    public boolean isValidLocation(int chunkX, int chunkZ) {
        /*
         * Villages start with a well, size 6x6, extending to the right and down
         * from the village spawn coordinates.  MapGenVillage.Start.Start() adds
         * only 2 to the multiplied chunk coordinate — hence the +2 offset below.
         */
        int wellSize = 6;
        int x1 = chunkX * 16 + 2;
        int z1 = chunkZ * 16 + 2;
        int x2 = x1 + wellSize - 1;
        int z2 = z1 + wellSize - 1;
        int wellX = (x1 + x2) / 2;
        int wellZ = (z1 + z2) / 2;

        /*
         * arbitraryConstant=2 gives a wellStructureSize of 4, which corresponds
         * to a 1-block lip around the well also being checked for invalid biomes
         * (at 1/4 block resolution).  Testing on seed -1364077613 shows this
         * correctly eliminates 18 % of false-positive village positions within
         * 20 km with only two known false negatives.
         */
        int arbitraryConstant = 2;
        int wellStructureSize = (x2 - x1) / 2 + arbitraryConstant;

        return biomeDataOracle.isValidBiomeForStructure(wellX, wellZ, wellStructureSize, validBiomes);
    }

    public Set<String> getValidBiomes() {
        return validBiomes;
    }
}
