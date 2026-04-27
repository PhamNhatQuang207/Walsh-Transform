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
        int size = BinaryVectorUtils.validatedDomainSize(f);
        int n = f.getDimension();
        double[] fVec = new double[size];

        for (int i = 0; i < size; i++) {
            int[] binaryVector = BinaryVectorUtils.intToBinaryVector(i, n);
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
}
