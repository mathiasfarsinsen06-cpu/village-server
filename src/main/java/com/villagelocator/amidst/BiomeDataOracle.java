package com.villagelocator.amidst;

import java.util.List;

/**
 * Interface for checking if biomes are valid for structure generation.
 * Validates that a structure's bounding box is in valid biomes.
 */
public interface BiomeDataOracle {
    
    /**
     * Check if biomes at a location are valid for structure generation.
     * 
     * @param seed world seed
     * @param centerX center X coordinate
     * @param centerZ center Z coordinate
     * @param structureSize size of structure area to check
     * @param validBiomes list of valid biome names for this structure
     * @return true if all checked biomes are valid
     */
    boolean isValidBiomeForStructure(long seed, int centerX, int centerZ, int structureSize, List<String> validBiomes);
    
    /**
     * Get the biome at the given coordinates.
     * 
     * @param seed world seed
     * @param x block X coordinate
     * @param z block Z coordinate
     * @return biome name
     */
    String getBiomeAt(long seed, int x, int z);
}
