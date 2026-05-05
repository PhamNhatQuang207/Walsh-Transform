package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class WalshTransformerDirectTest {

    @Test
    void computeSingleWeightRejectsNullFunction() {
        assertThrows(NullPointerException.class, () -> WalshTransformerDirect.computeSingleWeight(null, 0));
    }

    @Test
    void computeSingleWeightRejectsNegativeDimension() {
        BlackBoxFunction f = new BlackBoxFunction(-1) {
            @Override
            public double evaluate(boolean[] x) {
                return 0.0;
            }
        };

        assertThrows(IllegalArgumentException.class, () -> WalshTransformerDirect.computeSingleWeight(f, 0));
    }

    @Test
    void computeSingleWeightRejectsOutOfRangeK() {
        BlackBoxFunction f = new BlackBoxFunction(2) {
            @Override
            public double evaluate(boolean[] x) {
                return (x[0] ? 1 : 0) + (x[1] ? 1 : 0);
            }
        };

        assertThrows(IllegalArgumentException.class, () -> WalshTransformerDirect.computeSingleWeight(f, -1));
        assertThrows(IllegalArgumentException.class, () -> WalshTransformerDirect.computeSingleWeight(f, 4));
    }

    @Test
    void computeSingleWeightMatchesFullTransformCoefficient() {
        BlackBoxFunction f = new BlackBoxFunction(2) {
            @Override
            public double evaluate(boolean[] x) {
                return (x[0] ? 1 : 0) + 2.0 * (x[1] ? 1 : 0);
            }
        };

        double[] fullWeights = WalshTransformer.computeWeights(f);
        for (int k = 0; k < fullWeights.length; k++) {
            assertEquals(fullWeights[k], WalshTransformerDirect.computeSingleWeight(f, k), 1e-12);
        }
    }
    @Test
    void computeSingleWeightMatchesKnownCoefficient() {
        BlackBoxFunction f = new BlackBoxFunction(2) {
            private final double[] values = {1.0, 5.0, 3.0, 2.0};

            @Override
            public double evaluate(boolean[] x) {
                int idx = BinaryVectorUtils.binaryVectorToInt(x);
                return values[idx];
            }
        };
        assertEquals(2.75, WalshTransformerDirect.computeSingleWeight(f, 0), 1e-12);
        assertEquals(-0.75, WalshTransformerDirect.computeSingleWeight(f, 1), 1e-12);
        assertEquals(0.25, WalshTransformerDirect.computeSingleWeight(f, 2), 1e-12);
        assertEquals(-1.25, WalshTransformerDirect.computeSingleWeight(f, 3), 1e-12);
    }
}