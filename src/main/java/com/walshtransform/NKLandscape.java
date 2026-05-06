package com.walshtransform;

import java.util.Arrays;
import java.util.Random;

/**
 * Implements an NK fitness landscape as a black-box function over binary vectors.
 *
 * <p>Each variable contributes a value based on itself and {@code k} interacting variables.
 * The overall fitness is the average of all local contributions.</p>
 */
public class NKLandscape extends BlackBoxFunction {
    /** Number of interacting neighbors per variable (epistatic interactions). */
    private int k;
    /** Fitness contribution tables for each variable. */
    private double[][][] interactionTables;
    /** Indices of interacting variables for each position. */
    private int[][] interactions;

    /**
     * Creates a random NK landscape.
     *
     * @param n number of variables
     * @param k number of interactions per variable
     * @throws IllegalArgumentException if {@code k >= n}
     */
    public NKLandscape(int n, int k) {
        super(n);
        this.k = k;
        // Validate that k < n.
        if (k >= n) {
            throw new IllegalArgumentException("k must be less than n");
        }
        initializeLandscape();
    }

    private void initializeLandscape() {
        Random rand = new Random();
        interactions = new int[n][k + 1];
        // Each table stores random values in [0, 1) for all 2^(k+1) configurations.
        interactionTables = new double[n][(int) Math.pow(2, k + 1)][1];

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

            // Initialize the random contribution table for all k+1 combinations.
            for (int j = 0; j < Math.pow(2, k + 1); j++) {
                interactionTables[i][j][0] = rand.nextDouble();
            }
        }
    }

    private boolean contains(int[] array, int val, int limit) {
        for (int i = 0; i < limit; i++) {
            if (array[i] == val) return true;
        }
        return false;
    }

    @Override
    public double evaluate(boolean[] x) {
        double totalFitness = 0;
        // Fitness is the average contribution over all variables.
        for (int i = 0; i < n; i++) {
            int tableIndex = 0;
            for (int j = 0; j <= k; j++) {
                if (x[interactions[i][j]]) {
                    tableIndex += Math.pow(2, j);
                }
            }
            totalFitness += interactionTables[i][tableIndex][0];
        }
        return totalFitness / n;
    }

    /**
     * Builds a full textual description of the landscape configuration.
     *
     * @return formatted description including interactions and contribution tables
     */
    public String describeProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("NK Landscape properties\n");
        sb.append("n=").append(n).append(", k=").append(k).append('\n');
        sb.append("Interactions:\n");
        for (int i = 0; i < n; i++) {
            sb.append("  i=").append(i).append(" -> ")
                .append(Arrays.toString(interactions[i]))
                .append('\n');
        }
        sb.append("Contribution tables:\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < interactionTables[i].length; j++) {
                sb.append("  i=").append(i)
                    .append(", idx=").append(j)
                    .append(" => ").append(interactionTables[i][j][0])
                    .append('\n');
            }
        }
        return sb.toString();
    }

    int getK() {
        return k;
    }

    int[][] getInteractions() {
        return interactions;
    }

    double[][][] getInteractionTables() {
        return interactionTables;
    }
}
