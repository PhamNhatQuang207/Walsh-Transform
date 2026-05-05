package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NKLandscapeTest {

    @Test
    void constructorRejectsKGreaterOrEqualN() {
        assertThrows(IllegalArgumentException.class, () -> new NKLandscape(3, 3));
        assertThrows(IllegalArgumentException.class, () -> new NKLandscape(3, 4));
    }

    @Test
    void evaluateIsDeterministicForSameInput() {
        NKLandscape landscape = new NKLandscape(4, 1);
        boolean[] x = {true, false, true, false};

        double first = landscape.evaluate(x);
        double second = landscape.evaluate(x);

        assertEquals(first, second, 0.0);
    }

    @Test
    void evaluateReturnsValueInRangeZeroToOne() {
        NKLandscape landscape = new NKLandscape(5, 2);
        boolean[] x = {true, true, false, false, true};

        double value = landscape.evaluate(x);

        assertTrue(value >= 0.0 && value < 1.0, "Fitness should be in [0, 1)");
    }

    @Test
    void evaluateReturnsValueInRangeForAllInputs() {
        int n = 3;
        NKLandscape landscape = new NKLandscape(n, 1);
        int size = BinaryVectorUtils.checkedPowerOfTwo(n);

        for (int i = 0; i < size; i++) {
            boolean[] x = BinaryVectorUtils.intToBinaryVector(i, n);
            double value = landscape.evaluate(x);
            assertTrue(value >= 0.0 && value < 1.0, "Fitness should be in [0, 1)");
        }
    }

    @Test
    void evaluateThrowsForNullInput() {
        NKLandscape landscape = new NKLandscape(2, 1);

        assertThrows(NullPointerException.class, () -> landscape.evaluate(null));
    }

    @Test
    void constructorAllowsZeroInteractions() {
        assertDoesNotThrow(() -> new NKLandscape(4, 0));
    }
}
