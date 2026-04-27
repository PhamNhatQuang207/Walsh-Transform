package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BlackBoxFunctionTest {

    @Test
    void getDimensionReturnsConstructorValue() {
        BlackBoxFunction f = new BlackBoxFunction(4) {
            @Override
            public double evaluate(int[] x) {
                return 0.0;
            }
        };

        assertEquals(4, f.getDimension());
    }

    @Test
    void evaluateUsesSubclassImplementation() {
        BlackBoxFunction sumFunction = new BlackBoxFunction(3) {
            @Override
            public double evaluate(int[] x) {
                return x[0] + x[1] + x[2];
            }
        };

        assertEquals(2.0, sumFunction.evaluate(new int[] {1, 0, 1}));
    }
}
