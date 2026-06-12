package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.BitSet;
import java.util.Map;

import org.junit.jupiter.api.Test;

class BigNKLandscapeWalshTransformerTest {

    @Test
    void extractCoefficientsMatchesFastWalshForSmallN() {
        int n = 6;
        int k = 2;
        NKLandscape nk = new RandomNKLandscape(n, k);

        double[] expected = FastWalshTransformer.calculateWalshCoefficients(nk);
        Map<BitSet, Double> actual = BigNKLandscapeWalshTransformer.extractCoefficients(nk);

        int size = 1 << n;
        for (int mask = 0; mask < size; mask++) {
            BitSet key = toBitSet(mask, n);
            double value = actual.getOrDefault(key, 0.0);
            assertEquals(expected[mask], value, 1e-10);
        }

        int localSize = 1 << (k + 1);
        assertTrue(actual.size() <= n * localSize);
    }

    @Test
    void extractCoefficientsHandlesLargeN() {
        int n = 70;
        int k = 1;
        NKLandscape nk = new RandomNKLandscape(n, k);

        Map<BitSet, Double> actual = BigNKLandscapeWalshTransformer.extractCoefficients(nk);

        int localSize = 1 << (k + 1);
        assertTrue(actual.size() <= n * localSize);
    }

    private static BitSet toBitSet(int mask, int n) {
        BitSet bits = new BitSet(n);
        for (int bit = 0; bit < n; bit++) {
            if (((mask >> bit) & 1) != 0) {
                bits.set(bit);
            }
        }
        return bits;
    }
}
