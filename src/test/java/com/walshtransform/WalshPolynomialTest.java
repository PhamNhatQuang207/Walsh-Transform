package com.walshtransform;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WalshPolynomialTest {

    @Test
    void testReconstruction() {
        // Simple function f(x1, x2) = x1 + 2*x2
        BlackBoxFunction f = new BlackBoxFunction(2) {
            @Override
            public double evaluate(int[] x) {
                return x[0] + 2.0 * x[1];
            }
        };

        double[] weights = WalshTransformer.computeWeights(f);
        
        // Test all possible inputs for n=2
        for (int i = 0; i < 4; i++) {
            int[] x = BinaryVectorUtils.intToBinaryVector(i, 2);
            double expected = f.evaluate(x);
            double actual = WalshPolynomial.evaluate(weights, x);
            assertEquals(expected, actual, 1e-10, "Reconstructed value should match original for input " + i);
        }
    }

    @Test
    void testValidation() {
        double[] weights = new double[4]; // n=2
        int[] xSize3 = new int[3];
        
        assertThrows(IllegalArgumentException.class, () -> {
            WalshPolynomial.evaluate(weights, xSize3);
        }, "Should throw exception if weights length doesn't match 2^x.length");
    }

    @Test
    void testNullInputs() {
        assertThrows(NullPointerException.class, () -> WalshPolynomial.evaluate(null, new int[2]));
        assertThrows(NullPointerException.class, () -> WalshPolynomial.evaluate(new double[4], null));
    }
}
