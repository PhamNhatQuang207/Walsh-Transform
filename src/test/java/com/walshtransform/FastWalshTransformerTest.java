package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class FastWalshTransformerTest {

    @Test
    void calculateWalshCoefficientsMatchesWalshTransformer() {
        BlackBoxFunction f = new BlackBoxFunction(3) {
            @Override
            public double evaluate(int[] x) {
                return x[0] + 2.0 * x[1] - x[2];
            }
        };

        double[] expected = WalshTransformer.computeWeights(f);
        assertArrayEquals(expected, FastWalshTransformer.calculateWalshCoefficients(f), 1e-12);
        assertArrayEquals(expected, FastWalshTransformer.calculateWalshCoefficients(f, 3), 1e-12);
    }

    @Test
    void calculateWalshCoefficientsRejectsNullFunction() {
        assertThrows(NullPointerException.class, () -> FastWalshTransformer.calculateWalshCoefficients(null));
        assertThrows(NullPointerException.class, () -> FastWalshTransformer.calculateWalshCoefficients(null, 2));
    }

    @Test
    void calculateWalshCoefficientsRejectsDimensionMismatch() {
        BlackBoxFunction f = new BlackBoxFunction(2) {
            @Override
            public double evaluate(int[] x) {
                return x[0] + x[1];
            }
        };

        assertThrows(IllegalArgumentException.class, () -> FastWalshTransformer.calculateWalshCoefficients(f, 3));
    }

    @Test
    void computeSingleWeightMatchesKnownCoefficient() {
        BlackBoxFunction f = new BlackBoxFunction(2) {
            private final double[] values = {1.0, 5.0, 3.0, 2.0};

            @Override
            public double evaluate(int[] x) {
                int idx = BinaryVectorUtils.binaryVectorToInt(x);
                return values[idx];
            }
        };
        double[] expected = {2.75,-0.75,0.25,-1.25}; // Precomputed coefficients
        assertArrayEquals(expected, FastWalshTransformer.calculateWalshCoefficients(f), 1e-12);
    }
}
