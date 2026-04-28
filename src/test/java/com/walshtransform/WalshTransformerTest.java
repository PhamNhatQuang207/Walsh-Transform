package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WalshTransformerTest {

    @Test
    void computeWeightsRejectsNullFunction() {
        assertThrows(NullPointerException.class, () -> WalshTransformer.computeWeights(null));
    }

    @Test
    void computeWeightsRejectsNegativeDimension() {
        BlackBoxFunction f = new BlackBoxFunction(-1) {
            @Override
            public double evaluate(int[] x) {
                return 0.0;
            }
        };

        assertThrows(IllegalArgumentException.class, () -> WalshTransformer.computeWeights(f));
    }

    @Test
    void computeWeightsMatchesConstantFunction() {
        BlackBoxFunction constant = new BlackBoxFunction(2) {
            @Override
            public double evaluate(int[] x) {
                return 5.0;
            }
        };

        double[] weights = WalshTransformer.computeWeights(constant);
        assertArrayEquals(new double[] {5.0, 0.0, 0.0, 0.0}, weights, 1e-12);
    }

    @Test
    void computeWeightsPreservesFractionalCoefficient() {
        BlackBoxFunction halfConstant = new BlackBoxFunction(1) {
            @Override
            public double evaluate(int[] x) {
                return 0.5;
            }
        };

        double[] weights = WalshTransformer.computeWeights(halfConstant);
        assertEquals(0.5, weights[0], 1e-12);
        assertEquals(0.0, weights[1], 1e-12);
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
        double[] actual = WalshTransformer.computeWeights(f);
        assertArrayEquals(expected, actual, 1e-12);
    }
}