package com.villagelocator.amidst;

/**
 * Minecraft's JavaRandom implementation.
 * Used to generate deterministic pseudo-random numbers from a seed.
 * This is the EXACT same algorithm Minecraft uses internally.
 */
public class JavaRandom {
    private long seed;
    private static final long MULTIPLIER = 0x5DEECE66DL;
    private static final long ADDEND = 0xBL;
    private static final long MASK = (1L << 48) - 1;

    public JavaRandom(long seed) {
        setSeed(seed);
    }

    public void setSeed(long seed) {
        this.seed = (seed ^ MULTIPLIER) & MASK;
    }

    /**
     * Generate next random integer.
     * This is the core of Java's Linear Congruential Generator.
     */
    protected int next(int bits) {
        long oldseed, nextseed;
        oldseed = seed;
        nextseed = (oldseed * MULTIPLIER + ADDEND) & MASK;
        seed = nextseed;
        return (int) (nextseed >>> (48 - bits));
    }

    /**
     * Returns next random int.
     */
    public int nextInt() {
        return next(32);
    }

    /**
     * Returns next random int between 0 (inclusive) and n (exclusive).
     */
    public int nextInt(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n must be positive");

        if ((n & -n) == n)  // i.e., n is a power of 2
            return (int) ((n * (long) next(31)) >> 31);

        int bits, val;
        do {
            bits = next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }

    /**
     * Returns next random long.
     */
    public long nextLong() {
        return ((long) next(32) << 32) + next(32);
    }

    /**
     * Returns next random double between 0.0 and 1.0.
     */
    public double nextDouble() {
        return (((long) next(26) << 27) + next(27)) / (double) (1L << 53);
    }

    /**
     * Returns next random float between 0.0 and 1.0.
     */
    public float nextFloat() {
        return next(24) / ((float) (1 << 24));
    }

    /**
     * Returns next random boolean.
     */
    public boolean nextBoolean() {
        return next(1) != 0;
    }
}
