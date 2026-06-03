package com.villagelocator.biome;

import java.util.Set;

/**
 * Oracle that provides biome data for structure placement validation.
 * Based on AMIDST's BiomeDataOracle interface.
 */
public interface BiomeDataOracle {

    /**
     * Returns the biome name at the given block coordinates.
     */
    String getBiomeAt(int x, int z);

    /**
     * Checks whether all biomes within a square region centred at (x, z)
     * are present in {@code validBiomes}.
     *
     * <p>The region is scanned at quarter-resolution (4-block intervals),
     * matching Minecraft's internal structure-placement logic.</p>
     *
     * @param x           centre X in block coordinates
     * @param z           centre Z in block coordinates
     * @param size        half-size of the region in block coordinates
     * @param validBiomes set of biome names considered valid
     * @return {@code true} only if every sampled point is in {@code validBiomes}
     */
    boolean isValidBiomeForStructure(int x, int z, int size, Set<String> validBiomes);
}
