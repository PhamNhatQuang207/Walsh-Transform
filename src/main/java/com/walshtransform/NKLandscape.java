package com.walshtransform;

import java.util.Arrays;

/**
 * Implements an NK fitness landscape as a black-box function over binary vectors.
 *
 * <p>Each variable contributes a value based on itself and {@code k} interacting variables.
 * The overall fitness is the average of all local contributions.</p>
 */
public class NKLandscape extends BlackBoxFunction {
    /** Number of interacting neighbors per variable (epistatic interactions). */
    private final int k;
    /** Fitness contribution tables for each variable. */
    private final double[][][] interactionTables;
    /** Indices of interacting variables for each position. */
    private final int[][] interactions;

    /**
     * Creates a custom NK landscape with specified interactions and contribution tables.
     *
     * @param n number of variables
     * @param k number of interactions per variable
     * @param interactions indices of interacting variables for each position
     * @param interactionTables fitness contribution tables for each variable
     * @throws IllegalArgumentException if dimensions do not match or values are invalid
     * @throws NullPointerException if inputs are null
     */
    public NKLandscape(int n, int k, int[][] interactions, double[][][] interactionTables) {
        super(n);
        this.k = k;
        
        if (n <= 0) {
            throw new IllegalArgumentException("n must be greater than 0");
        }
        if (k < 0) {
            throw new IllegalArgumentException("k must be non-negative");
        }
        if (k >= n) {
            throw new IllegalArgumentException("k must be less than n");
        }
        if (interactions == null) {
            throw new NullPointerException("interactions must not be null");
        }
        if (interactionTables == null) {
            throw new NullPointerException("interactionTables must not be null");
        }
        if (interactions.length != n) {
            throw new IllegalArgumentException("interactions length must equal n");
        }
        if (interactionTables.length != n) {
            throw new IllegalArgumentException("interactionTables length must equal n");
        }
        int numConfigs = 1 << (k + 1);
        for (int i = 0; i < n; i++) {
            if (interactions[i] == null || interactions[i].length != k + 1) {
                throw new IllegalArgumentException("interactions[" + i + "] must have length " + (k + 1));
            }
            if (interactionTables[i] == null || interactionTables[i].length != numConfigs) {
                throw new IllegalArgumentException("interactionTables[" + i + "] must have length " + numConfigs);
            }
            for (int j = 0; j < numConfigs; j++) {
                if (interactionTables[i][j] == null || interactionTables[i][j].length != 1) {
                    throw new IllegalArgumentException("interactionTables[" + i + "][" + j + "] must have length 1");
                }
            }
        }
        
        // Defensive copying
        this.interactions = new int[n][k + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(interactions[i], 0, this.interactions[i], 0, k + 1);
        }
        
        this.interactionTables = new double[n][numConfigs][1];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < numConfigs; j++) {
                this.interactionTables[i][j][0] = interactionTables[i][j][0];
            }
        }
    }

    @Override
    public double evaluate(boolean[] x) {
        if (x == null) {
            throw new NullPointerException("x must not be null");
        }
        if (x.length != n) {
            throw new IllegalArgumentException("x must have length " + n);
        }
        double totalFitness = 0;
        // Fitness is the average contribution over all variables.
        for (int i = 0; i < n; i++) {
            int tableIndex = 0;
            for (int j = 0; j <= k; j++) {
                if (x[interactions[i][j]]) {
                    tableIndex += 1 << j;
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

    public int getK() {
        return k;
    }

    public int[][] getInteractions() {
        return interactions;
    }

    public double[][][] getInteractionTables() {
        return interactionTables;
    }
}

