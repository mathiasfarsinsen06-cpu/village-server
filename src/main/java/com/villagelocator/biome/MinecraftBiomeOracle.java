package com.villagelocator.biome;

import java.util.Set;

/**
 * BiomeDataOracle for Minecraft 1.21.4.
 *
 * <p>Uses seed-based multi-octave value noise to approximate Minecraft's
 * multi-noise biome placement system. Three independent noise channels
 * are sampled:</p>
 * <ol>
 *   <li><b>Continentalness</b> – ocean vs. land (positive = land).
 *       Minecraft 1.18+ oceans cover roughly 35 % of the surface.</li>
 *   <li><b>Temperature</b> – cold (−1) to hot (+1).</li>
 *   <li><b>Humidity</b> – dry (−1) to wet (+1).</li>
 * </ol>
 *
 * <p>Village-compatible biomes:
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
public class MinecraftBiomeOracle implements BiomeDataOracle {

    /**
     * Approximate biome width in blocks. Minecraft biomes range from
     * a few hundred to several thousand blocks; 1 500 is a reasonable
     * middle ground that keeps the noise grid at a manageable resolution.
     */
    private static final double BIOME_SCALE = 1500.0;

    /**
     * Continentalness noise is sampled at a coarser scale so that
     * continent-sized land masses are produced (comparable to ~5 000-block
     * continent widths in Minecraft 1.18+).
     */
    private static final double CONTINENT_SCALE = 3000.0;

    private final long temperatureSeed;
    private final long humiditySeed;
    private final long continentalnessSeed;

    public MinecraftBiomeOracle(long worldSeed) {
        this.temperatureSeed     = avalanche(worldSeed ^ 0x4456C3DF07B0142L);
        this.humiditySeed        = avalanche(worldSeed ^ 0xA0761D6478BD642FL);
        this.continentalnessSeed = avalanche(worldSeed ^ 0xD6E8FEB86659FD93L);
    }

    // -----------------------------------------------------------------------
    // BiomeDataOracle implementation
    // -----------------------------------------------------------------------

    @Override
    public String getBiomeAt(int x, int z) {
        double continentalness = sampleNoise(continentalnessSeed, x / CONTINENT_SCALE, z / CONTINENT_SCALE);
        double temperature     = sampleNoise(temperatureSeed,     x / BIOME_SCALE,     z / BIOME_SCALE);
        double humidity        = sampleNoise(humiditySeed,        x / BIOME_SCALE,     z / BIOME_SCALE);
        return MinecraftBiomeRegistry.normalizeBiomeName(classifyBiome(continentalness, temperature, humidity));
    }

    @Override
    public boolean isValidBiomeForStructure(int x, int z, int size, Set<String> validBiomes) {
        // Convert to quarter-resolution coordinates and scan the region.
        // The >> 2 matches Minecraft's "areBiomesViable" which works at 1/4 resolution.
        int left   = (x - size) >> 2;
        int top    = (z - size) >> 2;
        int right  = (x + size) >> 2;
        int bottom = (z + size) >> 2;

        for (int bx = left; bx <= right; bx++) {
            for (int bz = top; bz <= bottom; bz++) {
                String biome = getBiomeAt(bx << 2, bz << 2);
                if (!validBiomes.contains(biome)) {
                    return false;
                }
            }
        }
        return true;
    }

    // -----------------------------------------------------------------------
    // Biome classification
    // -----------------------------------------------------------------------

    /**
     * Maps continentalness / temperature / humidity to a Minecraft biome name.
     *
     * <p>Thresholds are tuned so that:
     * <ul>
     *   <li>~35 % of the surface is ocean or coastal (not valid for villages).</li>
     *   <li>The remaining land is split between village-valid biomes
     *       (plains, savanna, desert, taiga, snowy_plains, cherry_grove) and non-village biomes
     *       (forest, jungle, swamp, etc.) in proportions similar to Minecraft 1.21.4.</li>
     * </ul>
     * </p>
     */
    private String classifyBiome(double continentalness, double temperature, double humidity) {
        // Negative continentalness → ocean / deep ocean
        if (continentalness < -0.15) return "ocean";

        // Near-shore areas have reduced land biome diversity — treat as non-village
        if (continentalness < 0.05) return "beach";

        if (temperature > 0.40) {
            if (humidity < -0.10) return "desert";
            if (humidity <  0.35) return "savanna";
            return "jungle";
        } else if (temperature > 0.05) {
            // Temperate zone: plains when dry, cherry_grove when very humid, forest otherwise
            if (humidity < 0.00) return "plains";
            if (humidity > 0.55) return "cherry_grove";
            return "forest";
        } else if (temperature > -0.35) {
            // Cold zone: taiga when moderately moist; forest when wet
            if (humidity > 0.35) return "forest";
            return "taiga";
        } else {
            // Frozen zone
            if (humidity > 0.30) return "snowy_taiga";
            return "snowy_plains";
        }
    }

    // -----------------------------------------------------------------------
    // Noise engine — multi-octave value noise with smooth interpolation
    // -----------------------------------------------------------------------

    /**
     * Returns a value in {@code [-1, 1]} using 3-octave value noise.
     */
    private double sampleNoise(long seed, double x, double z) {
        double result         = 0.0;
        double amplitude      = 1.0;
        double frequency      = 1.0;
        double totalAmplitude = 0.0;

        for (int octave = 0; octave < 3; octave++) {
            long octaveSeed = avalanche(seed + (long) octave * 987_654_321L);
            result         += interpolatedNoise(octaveSeed, x * frequency, z * frequency) * amplitude;
            totalAmplitude += amplitude;
            amplitude      *= 0.5;
            frequency      *= 2.0;
        }

        return result / totalAmplitude;
    }

    /** Bilinearly interpolated grid noise. */
    private double interpolatedNoise(long seed, double x, double z) {
        int    ix = (int) Math.floor(x);
        int    iz = (int) Math.floor(z);
        double fx = x - ix;
        double fz = z - iz;

        double v00 = gridNoise(seed, ix,     iz);
        double v10 = gridNoise(seed, ix + 1, iz);
        double v01 = gridNoise(seed, ix,     iz + 1);
        double v11 = gridNoise(seed, ix + 1, iz + 1);

        double tx = smoothstep(fx);
        double tz = smoothstep(fz);

        return lerp(lerp(v00, v10, tx), lerp(v01, v11, tx), tz);
    }

    /** Deterministic pseudo-random value at integer grid point (x, z). */
    private double gridNoise(long seed, int x, int z) {
        long h = seed;
        h ^= (long) x * 0x9E3779B97F4A7C15L;
        h ^= (long) z * 0x6C62272E07BB0142L;
        h = avalanche(h);
        // Map to [-1, 1]
        return (h & 0x7FFFFFFFFFFFFFFFL) / (double) Long.MAX_VALUE * 2.0 - 1.0;
    }

    /** MurmurHash3 64-bit finaliser — good avalanche properties. */
    private long avalanche(long h) {
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        return h ^ (h >>> 31);
    }

    private double smoothstep(double t) {
        return t * t * (3.0 - 2.0 * t);
    }

    private double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
}
