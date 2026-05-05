package com.walshtransform;

import java.util.Objects;

/**
 * Provides methods to evaluate a function reconstructed from its Walsh coefficients.
 */
public final class WalshPolynomial {

    private WalshPolynomial() {
        // Utility class; prevent instantiation.
    }

    /**
     * Reconstructs the value f(x) from the Walsh coefficients (weights).
     *
     * <p>The reconstruction formula is: f(x) = sum_{k=0}^{2^n-1} w_k * (-1)^(k dot x)</p>
     *
     * @param weights array of Walsh coefficients w_k
    * @param x input binary vector
     * @return reconstructed value f(x)
     * @throws NullPointerException if weights or x is null
     * @throws IllegalArgumentException if weights.length is not 2^(x.length)
     */
    public static double evaluate(double[] weights, boolean[] x) {
        Objects.requireNonNull(weights, "weights must not be null");
        Objects.requireNonNull(x, "input vector x must not be null");

        int n = x.length;
        // Validate that weights.length is 2^n
        if (weights.length != (1 << n)) {
            throw new IllegalArgumentException(
                String.format("Weights length (%d) must be 2^(x.length) (2^%d = %d)", 
                weights.length, n, 1 << n));
        }

        int xVal = BinaryVectorUtils.binaryVectorToInt(x);
        double result = 0;

        for (int k = 0; k < weights.length; k++) {
            // Optimization: skip near-zero weights to improve performance and avoid noise
            if (Math.abs(weights[k]) > 1e-15) {
                result += weights[k] * calculateWalshFunction(k, xVal);
            }
        }
        return result;
    }

    /**
     * Computes the Walsh function phi_k(x) = (-1)^(k dot x).
     *
     * @param k index of the Walsh function
     * @param xVal integer representation of the input vector x
     * @return 1 if k dot x is even, -1 if k dot x is odd
     */
    private static int calculateWalshFunction(int k, int xVal) {
        // Dot product modulo 2 is equivalent to the parity of the bitwise AND
        int dotProduct = Integer.bitCount(k & xVal);
        return (dotProduct % 2 == 0) ? 1 : -1;
    }
}
