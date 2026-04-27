package com.walshtransform;

/**
 * Computes a single Walsh coefficient directly from function evaluations.
 */
public class WalshTransformerDirect {
    private WalshTransformerDirect() {
        // Utility class; prevent instantiation.
    }

    /**
     * Computes one normalized Walsh coefficient for index {@code k}.
     *
     * @param f function to transform
     * @param k Walsh index in range {@code [0, 2^n - 1]}
     * @return normalized coefficient value for index {@code k}
     * @throws NullPointerException if {@code f} is null
     * @throws IllegalArgumentException if the function dimension is invalid or {@code k} is out of range
     */
    public static double computeSingleWeight(BlackBoxFunction f, int k) {
        int size = BinaryVectorUtils.validatedDomainSize(f);
        int n = f.getDimension();
        if (k < 0 || k >= size) {
            throw new IllegalArgumentException("k must be in range [0, 2^n - 1]");
        }

        double sum = 0;

        for (int x = 0; x < size; x++) {
            int[] binaryX = BinaryVectorUtils.intToBinaryVector(x, n);
            double fx = f.evaluate(binaryX);
            sum += fx * calculateWalshValue(k, binaryX);
        }

        return sum / size;
    }

    /**
     * Computes the Walsh basis value {@code (-1)^(k.x)} where dot product is taken modulo 2.
     *
     * @param k Walsh index
     * @param x binary input vector
     * @return {@code 1} when parity is even, otherwise {@code -1}
     */
    private static int calculateWalshValue(int k, int[] x) {
        int dotProduct = 0;
        for (int i = 0; i < x.length; i++) {
            if (((k >> i) & 1) == 1) {
                dotProduct += x[i];
            }
        }
        return (dotProduct % 2 == 0) ? 1 : -1;
    }
}
