package com.walshtransform;

import java.util.Random;

/**
 * Implements a random NK fitness landscape.
 *
 * <p>Each variable is randomly assigned k interacting variables, and the
 * fitness contribution tables are filled with random values in [0, 1).</p>
 */
public class RandomNKLandscape extends NKLandscape {

    /**
     * Creates a random NK landscape.
     *
     * @param n number of variables
     * @param k number of interactions per variable
     * @throws IllegalArgumentException if {@code k >= n}
     */
    public RandomNKLandscape(int n, int k) {
        this(n, k, new Random());
    }

    /**
     * Creates a random NK landscape with a specific seed for reproducibility.
     *
     * @param n number of variables
     * @param k number of interactions per variable
     * @param seed the seed for random number generation
     * @throws IllegalArgumentException if {@code k >= n}
     */
    public RandomNKLandscape(int n, int k, long seed) {
        this(n, k, new Random(seed));
    }

    /**
     * Creates a random NK landscape with a specific Random instance.
     *
     * @param n number of variables
     * @param k number of interactions per variable
     * @param rand the Random instance
     * @throws IllegalArgumentException if {@code k >= n}
     */
    public RandomNKLandscape(int n, int k, Random rand) {
        super(n, k, generateRandomInteractions(n, k, rand), generateRandomInteractionTables(n, k, rand));
    }

    private static int[][] generateRandomInteractions(int n, int k, Random rand) {
        if (k >= n) {
            throw new IllegalArgumentException("k must be less than n");
        }
        int[][] interactions = new int[n][k + 1];
        for (int i = 0; i < n; i++) {
            // Pick variable i and k distinct neighbors at random.
            interactions[i][0] = i;
            for (int j = 1; j <= k; j++) {
                int neighbor;
                do {
                    neighbor = rand.nextInt(n);
                } while (contains(interactions[i], neighbor, j));
                interactions[i][j] = neighbor;
            }
        }
        return interactions;
    }

    private static boolean contains(int[] array, int val, int limit) {
        for (int i = 0; i < limit; i++) {
            if (array[i] == val) {
                return true;
            }
        }
        return false;
    }

    private static double[][][] generateRandomInteractionTables(int n, int k, Random rand) {
        if (k >= n) {
            throw new IllegalArgumentException("k must be less than n");
        }
        int numConfigs = 1 << (k + 1);
        double[][][] interactionTables = new double[n][numConfigs][1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < numConfigs; j++) {
                interactionTables[i][j][0] = rand.nextDouble();
            }
        }
        return interactionTables;
    }
}
