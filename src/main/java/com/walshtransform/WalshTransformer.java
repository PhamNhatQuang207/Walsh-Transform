package com.walshtransform;

/**
 * Computes Walsh transform coefficients for a {@link BlackBoxFunction}.
 *
 * <p>The implementation evaluates the function over all binary vectors of dimension {@code n},
 * multiplies by the Sylvester Hadamard matrix of order {@code 2^n}, and normalizes by
 * {@code 2^n}.</p>
 */
public class WalshTransformer {
    private WalshTransformer() {
        // Utility class; prevent instantiation.
    }

    /**
     * Computes normalized Walsh coefficients for the provided function.
     *
     * @param f function to transform
     * @return array of normalized Walsh coefficients of length {@code 2^n}
     * @throws IllegalArgumentException if the function dimension is negative or too large
     * @throws NullPointerException if {@code f} is null
     */
    public static double[] computeWeights(BlackBoxFunction f) {
        if (f == null) {
            throw new NullPointerException("f must not be null");
        }

        int n = f.getDimension();
        if (n < 0) {
            throw new IllegalArgumentException("dimension n must be >= 0");
        }
        if (n >= Integer.SIZE - 1) {
            throw new IllegalArgumentException("dimension n is too large to build a 2^n vector size as int");
        }

        int size = 1 << n; // 2^n
        double[] fVec = new double[size];

        for (int i = 0; i < size; i++) {
            int[] binaryVector = intToBinaryVector(i, n);
            fVec[i] = f.evaluate(binaryVector);
        }

        int[][] hn = HadamardMatrix.generate(n);
        double[] weights = new double[size];

        for (int i = 0; i < size; i++) {
            double sum = 0.0;
            for (int j = 0; j < size; j++) {
                sum += hn[i][j] * fVec[j];
            }
            weights[i] = sum / size;
        }

        return weights;
    }

    /**
     * Encodes an integer value as a binary vector of length {@code n}.
     *
     * <p>Bit {@code i} is mapped to {@code vector[i]}.</p>
     *
     * @param value value to encode
     * @param n vector length
     * @return binary vector representation of {@code value}
     */
    private static int[] intToBinaryVector(int value, int n) {
        int[] vector = new int[n];
        for (int i = 0; i < n; i++) {
            vector[i] = (value >> i) & 1;
        }
        return vector;
    }

}
