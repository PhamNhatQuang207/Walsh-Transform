package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NKLandscapeTest {

    @Test
    void constructorRejectsKGreaterOrEqualN() {
        assertThrows(IllegalArgumentException.class, () -> new RandomNKLandscape(3, 3));
        assertThrows(IllegalArgumentException.class, () -> new RandomNKLandscape(3, 4));
    }

    @Test
    void evaluateIsDeterministicForSameInput() {
        NKLandscape landscape = new RandomNKLandscape(4, 1);
        boolean[] x = {true, false, true, false};

        double first = landscape.evaluate(x);
        double second = landscape.evaluate(x);

        assertEquals(first, second, 0.0);
    }

    @Test
    void evaluateReturnsValueInRangeZeroToOne() {
        NKLandscape landscape = new RandomNKLandscape(5, 2);
        boolean[] x = {true, true, false, false, true};

        double value = landscape.evaluate(x);

        assertTrue(value >= 0.0 && value < 1.0, "Fitness should be in [0, 1)");
    }

    @Test
    void evaluateReturnsValueInRangeForAllInputs() {
        int n = 3;
        NKLandscape landscape = new RandomNKLandscape(n, 1);
        int size = BinaryVectorUtils.checkedPowerOfTwo(n);

        for (int i = 0; i < size; i++) {
            boolean[] x = BinaryVectorUtils.intToBinaryVector(i, n);
            double value = landscape.evaluate(x);
            assertTrue(value >= 0.0 && value < 1.0, "Fitness should be in [0, 1)");
        }
    }

    @Test
    void evaluateThrowsForNullInput() {
        NKLandscape landscape = new RandomNKLandscape(2, 1);

        assertThrows(NullPointerException.class, () -> landscape.evaluate(null));
    }

    @Test
    void constructorAllowsZeroInteractions() {
        assertDoesNotThrow(() -> new RandomNKLandscape(4, 0));
    }

    @Test
    void customConstructorValidatesInputAndDefensivelyCopies() {
        int n = 3;
        int k = 1;
        int[][] interactions = {
            {0, 1},
            {1, 2},
            {2, 0}
        };
        double[][][] tables = {
            {{0.1}, {0.2}, {0.3}, {0.4}},
            {{0.5}, {0.6}, {0.7}, {0.8}},
            {{0.9}, {0.15}, {0.25}, {0.35}}
        };

        NKLandscape landscape = new NKLandscape(n, k, interactions, tables);
        assertEquals(n, landscape.getDimension());
        assertEquals(k, landscape.getK());

        // Verify defensive copy by modifying the input arrays
        interactions[0][1] = 99;
        tables[0][0][0] = 99.9;

        assertEquals(1, landscape.getInteractions()[0][1]);
        assertEquals(0.1, landscape.getInteractionTables()[0][0][0], 1e-9);

        // Verify evaluation
        // x = [false, false, false]
        // i=0: interactions[0] = [0, 1] => x[0] is false, x[1] is false => local table index = 0 => value = 0.1
        // i=1: interactions[1] = [1, 2] => x[1] is false, x[2] is false => local table index = 0 => value = 0.5
        // i=2: interactions[2] = [2, 0] => x[2] is false, x[0] is false => local table index = 0 => value = 0.9
        // total = 0.1 + 0.5 + 0.9 = 1.5. average = 0.5
        assertEquals(0.5, landscape.evaluate(new boolean[]{false, false, false}), 1e-9);

        // x = [true, false, false]
        // i=0: interactions[0] = [0, 1] => x[0] is true, x[1] is false => local table index = 1 => value = 0.2
        // i=1: interactions[1] = [1, 2] => x[1] is false, x[2] is false => local table index = 0 => value = 0.5
        // i=2: interactions[2] = [2, 0] => x[2] is false, x[0] is true => local table index = 2 => value = 0.25
        // total = 0.2 + 0.5 + 0.25 = 0.95. average = 0.95 / 3 = 0.31666666666
        assertEquals(0.95 / 3.0, landscape.evaluate(new boolean[]{true, false, false}), 1e-9);
    }

    @Test
    void customConstructorRejectsInvalidDimensions() {
        int[][] validInteractions = {{0, 1}, {1, 2}, {2, 0}};
        double[][][] validTables = {
            {{0.1}, {0.2}, {0.3}, {0.4}},
            {{0.5}, {0.6}, {0.7}, {0.8}},
            {{0.9}, {0.15}, {0.25}, {0.35}}
        };

        // Null checks
        assertThrows(NullPointerException.class, () -> new NKLandscape(3, 1, null, validTables));
        assertThrows(NullPointerException.class, () -> new NKLandscape(3, 1, validInteractions, null));

        // Wrong row counts
        assertThrows(IllegalArgumentException.class, () -> new NKLandscape(3, 1, new int[2][2], validTables));
        assertThrows(IllegalArgumentException.class, () -> new NKLandscape(3, 1, validInteractions, new double[2][4][1]));

        // Wrong interaction column length
        int[][] invalidInteractions = {{0, 1}, {1}, {2, 0}};
        assertThrows(IllegalArgumentException.class, () -> new NKLandscape(3, 1, invalidInteractions, validTables));

        // Wrong table size
        double[][][] invalidTables = {
            {{0.1}, {0.2}, {0.3}}, // length 3, should be 4
            {{0.5}, {0.6}, {0.7}, {0.8}},
            {{0.9}, {0.15}, {0.25}, {0.35}}
        };
        assertThrows(IllegalArgumentException.class, () -> new NKLandscape(3, 1, validInteractions, invalidTables));
    }
}
