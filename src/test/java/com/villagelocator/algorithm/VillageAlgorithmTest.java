package com.villagelocator.algorithm;

import com.villagelocator.biome.BiomeDataOracle;
import com.villagelocator.biome.MinecraftBiomeRegistry;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VillageAlgorithmTest {

    @Test
    void isValidLocationChecksAmidstWellArea() {
        RecordingBiomeDataOracle oracle = new RecordingBiomeDataOracle();
        VillageAlgorithm algorithm = new VillageAlgorithm(oracle);

        boolean valid = algorithm.isValidLocation(3, -2);

        assertTrue(valid);
        assertEquals(52, oracle.x);
        assertEquals(-27, oracle.z);
        assertEquals(4, oracle.size);
        assertSame(MinecraftBiomeRegistry.VALID_VILLAGE_BIOMES, oracle.validBiomes);
    }

    private static final class RecordingBiomeDataOracle implements BiomeDataOracle {
        private int x;
        private int z;
        private int size;
        private Set<String> validBiomes;

        @Override
        public String getBiomeAt(int x, int z) {
            return "plains";
        }

        @Override
        public boolean isValidBiomeForStructure(int x, int z, int size, Set<String> validBiomes) {
            this.x = x;
            this.z = z;
            this.size = size;
            this.validBiomes = validBiomes;
            return true;
        }
    }
}
