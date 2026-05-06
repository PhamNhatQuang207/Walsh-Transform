package com.walshtransform;

public final class FastWalshTransformer {

    private FastWalshTransformer() {
        // Utility class; prevent instantiation.
    }

    /**
     * Core method: performs the Fast Walsh-Hadamard Transform in-place.
     * Transforms the input array using the implicit Hadamard matrix without allocating one.
     */
    static void fwht(double[] a) {
        int n = a.length;
        if (n == 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException("array length must be a power of two");
        }
        
        // 'len' is the pairing distance (1, 2, 4, 8, ...).
        for (int len = 1; 2 * len <= n; len *= 2) {
            
            // Iterate over each block in the array.
            for (int i = 0; i < n; i += 2 * len) {
                
                // Pair elements inside the block.
                for (int j = 0; j < len; j++) {
                    double u = a[i + j];             // Element A
                    double v = a[i + len + j];       // Element B at distance 'len'
                    
                    // Apply the (A+B, A-B) butterfly in-place.
                    a[i + j] = u + v;
                    a[i + len + j] = u - v;
                }
            }
        }
    }

    /**
     * Main method to compute Walsh coefficients from a black-box function.
     * Based on: w = (1 / 2^n) * H_n * f
     */
    public static double[] calculateWalshCoefficients(BlackBoxFunction f, int numVariables) {
        if (f == null) {
            throw new NullPointerException("f must not be null");
        }
        int n = f.getDimension();
        if (n != numVariables) {
            throw new IllegalArgumentException("numVariables must match function dimension");
        }
        int totalSolutions = BinaryVectorUtils.checkedPowerOfTwo(n); // Array size N = 2^n
        double[] fitnessValues = new double[totalSolutions];

        // Step 1: Collect all 2^n values from the black-box function.
        for (int i = 0; i < totalSolutions; i++) {
            boolean[] binaryVector = BinaryVectorUtils.intToBinaryVector(i, n);
            fitnessValues[i] = f.evaluate(binaryVector);
        }

        // Step 2: Run FWHT to implicitly multiply by the Hadamard matrix.
        fwht(fitnessValues);

        // Step 3: Normalize by dividing all entries by 2^n.
        double scale = 1.0 / totalSolutions;
        for (int i = 0; i < totalSolutions; i++) {
            fitnessValues[i] *= scale;
        }

        // The array now contains all Walsh coefficients (w_0 to w_{2^n-1}).
        return fitnessValues;
    }

    /**
     * Convenience overload that uses the function's declared dimension.
     */
    public static double[] calculateWalshCoefficients(BlackBoxFunction f) {
        int size = BinaryVectorUtils.validatedDomainSize(f);
        int n = f.getDimension();
        double[] fitnessValues = new double[size];

        for (int i = 0; i < size; i++) {
            boolean[] binaryVector = BinaryVectorUtils.intToBinaryVector(i, n);
            fitnessValues[i] = f.evaluate(binaryVector);
        }

        fwht(fitnessValues);

        double scale = 1.0 / size;
        for (int i = 0; i < size; i++) {
            fitnessValues[i] *= scale;
        }

        return fitnessValues;
    }
}
