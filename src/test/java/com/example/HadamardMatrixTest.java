package com.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class HadamardMatrixTest {

    @Test
    void generateN0ReturnsSingleOne() {
        int[][] h = HadamardMatrix.generate(0);

        assertEquals(1, h.length);
        assertEquals(1, h[0].length);
        assertEquals(1, h[0][0]);
    }

    @Test
    void generateN1MatchesExpectedSylvesterForm() {
        int[][] h = HadamardMatrix.generate(1);

        assertArrayEquals(new int[] {1, 1}, h[0]);
        assertArrayEquals(new int[] {1, -1}, h[1]);
    }
    @Test
    void generateN2MatchesExpectedSylvesterForm() {
        int[][] h = HadamardMatrix.generate(2);

        assertArrayEquals(new int[] {1, 1, 1, 1}, h[0]);
        assertArrayEquals(new int[] {1, -1, 1, -1}, h[1]);
        assertArrayEquals(new int[] {1, 1, -1, -1}, h[2]);
        assertArrayEquals(new int[] {1, -1, -1, 1}, h[3]);
    }
    @Test
    void generateN2RowsAreOrthogonal() {
        int[][] h = HadamardMatrix.generate(2);
        int size = h.length;

        assertEquals(4, size);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int dot = 0;
                for (int k = 0; k < size; k++) {
                    dot += h[i][k] * h[j][k];
                }

                if (i == j) {
                    assertEquals(size, dot);
                } else {
                    assertEquals(0, dot);
                }
            }
        }
    }

    @Test
    void generateRejectsNegativeN() {
        assertThrows(IllegalArgumentException.class, () -> HadamardMatrix.generate(-1));
    }

    @Test
    void generateRejectsTooLargeN() {
        assertThrows(IllegalArgumentException.class, () -> HadamardMatrix.generate(31));
    }
}
