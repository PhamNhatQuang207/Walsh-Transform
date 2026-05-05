package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BlackBoxFunctionTest {

    @Test
    void getDimensionReturnsConstructorValue() {
        BlackBoxFunction f = new BlackBoxFunction(4) {
            @Override
            public double evaluate(boolean[] x) {
                return 0.0;
            }
        };

        assertEquals(4, f.getDimension());
    }

    @Test
    void evaluateUsesSubclassImplementation() {
        BlackBoxFunction sumFunction = new BlackBoxFunction(3) {
            @Override
            public double evaluate(boolean[] x) {
                return (x[0] ? 1 : 0) + (x[1] ? 1 : 0) + (x[2] ? 1 : 0);
            }
        };

        assertEquals(2.0, sumFunction.evaluate(new boolean[] {true, false, true}));
    }
}
