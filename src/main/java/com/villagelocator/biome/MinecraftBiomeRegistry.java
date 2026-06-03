package com.villagelocator.biome;

import java.util.Map;
import java.util.Set;

public final class MinecraftBiomeRegistry {

    public static final Set<String> VALID_VILLAGE_BIOMES = Set.of(
        "plains",
        "savanna",
        "desert",
        "taiga",
        "snowy_plains",
        "cherry_grove"
    );

    private static final Map<String, String> BIOME_ALIASES = Map.of(
        "snowy_tundra", "snowy_plains"
    );

    private MinecraftBiomeRegistry() {
    }

    public static String normalizeBiomeName(String biomeName) {
        if (biomeName == null) {
            return "";
        }
        return BIOME_ALIASES.getOrDefault(biomeName, biomeName);
    }

    public static boolean isValidVillageBiome(String biomeName) {
        return VALID_VILLAGE_BIOMES.contains(normalizeBiomeName(biomeName));
    }
}
