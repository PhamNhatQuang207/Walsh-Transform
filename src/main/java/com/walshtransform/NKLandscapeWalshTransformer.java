package com.walshtransform;

import java.util.HashMap;
import java.util.Map;

/**
 * Grey-box Walsh coefficient extractor for NK landscapes.
 */
public final class NKLandscapeWalshTransformer {

    private NKLandscapeWalshTransformer() {
        // Utility class; prevent instantiation.
    }

    /**
     * Extracts sparse Walsh coefficients by exploiting NK landscape structure.
     */
    public static Map<Long, Double> extractCoefficients(NKLandscape nk) {
        if (nk == null) {
            throw new NullPointerException("nk must not be null");
        }
        int n = nk.getDimension();
        if (n >= Long.SIZE) {
            throw new IllegalArgumentException("n must be < 64 to fit in a long mask");
        }

        int k = nk.getK();
        int localSize = 1 << (k + 1); // Size of each local truth table: 2^(k+1)
        double[][][] tables = nk.getInteractionTables();
        int[][] interactions = nk.getInteractions();

        // Sparse map of global Walsh coefficients: mask -> coefficient value.
        Map<Long, Double> coefficients = new HashMap<>(n * localSize);
        double localScale = 1.0 / localSize; // FWHT normalization for local tables

        for (int i = 0; i < n; i++) {
            // Copy the local contribution table for f_i into a flat array.
            double[] local = new double[localSize];
            for (int idx = 0; idx < localSize; idx++) {
                local[idx] = tables[i][idx][0];
            }

            // Transform local values into local Walsh coefficients in-place.
            FastWalshTransformer.fwht(local);

            for (int idx = 0; idx < localSize; idx++) {
                // Normalize local Walsh coefficient for this local mask.
                double value = local[idx] * localScale;
                if (Math.abs(value) < 1e-12) {
                    continue;
                }

                // Map local mask bits to their global variable positions.
                long globalMask = 0L;
                int localMask = idx;
                for (int bit = 0; bit <= k; bit++) {
                    if ((localMask & 1) != 0) {
                        globalMask |= 1L << interactions[i][bit];
                    }
                    localMask >>= 1;
                }

                // Accumulate into the sparse global coefficient map.
                coefficients.merge(globalMask, value, Double::sum);
            }
        }

        // NK fitness is the average of n local contributions.
        double globalScale = 1.0 / n;
        for (Map.Entry<Long, Double> entry : coefficients.entrySet()) {
            entry.setValue(entry.getValue() * globalScale);
        }

        return coefficients;
    }
}
