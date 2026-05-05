package com.walshtransform;

/**
 * Utility methods for binary-vector encoding and powers of two used by Walsh transforms.
 */
final class BinaryVectorUtils {
    private BinaryVectorUtils() {
        // Utility class; prevent instantiation.
    }

    /**
     * Validates a Walsh function input and returns its domain vector size {@code 2^n}.
     *
     * @param f function to validate
     * @return domain size {@code 2^n}
     * @throws NullPointerException if {@code f} is null
     * @throws IllegalArgumentException if function dimension is negative or too large
     */
    static int validatedDomainSize(BlackBoxFunction f) {
        if (f == null) {
            throw new NullPointerException("f must not be null");
        }
        return checkedPowerOfTwo(f.getDimension());
    }

    /**
     * Computes {@code 2^n} using bit shifting after validating bounds.
     *
     * @param n non-negative exponent
     * @return {@code 2^n} as an int
     * @throws IllegalArgumentException if {@code n} is negative or too large for int sizing
     */
    static int checkedPowerOfTwo(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("dimension n must be >= 0");
        }
        if (n >= Integer.SIZE - 1) {
            throw new IllegalArgumentException("dimension n is too large to build a 2^n vector size as int");
        }
        return 1 << n;
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
    static boolean[] intToBinaryVector(int value, int n) {
        boolean[] vector = new boolean[n];
        for (int i = 0; i < n; i++) {
            vector[i] = ((value >> i) & 1) == 1;
        }
        return vector;
    }

    /**
     * Converts a binary vector back to its integer representation.
     *
     * @param vector binary vector
     * @return integer value
     */
    static int binaryVectorToInt(boolean[] vector) {
        int value = 0;
        for (int i = 0; i < vector.length; i++) {
            if (vector[i]) {
                value |= (1 << i);
            }
        }
        return value;
    }
    }