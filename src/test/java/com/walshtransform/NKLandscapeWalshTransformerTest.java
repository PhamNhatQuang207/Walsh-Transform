package com.walshtransform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

class NKLandscapeWalshTransformerTest {

    @Test
    void extractCoefficientsMatchesFastWalshForSmallN() {
        int n = 5;
        int k = 2;
        NKLandscape nk = new NKLandscape(n, k);

        double[] expected = FastWalshTransformer.calculateWalshCoefficients(nk);
        Map<Long, Double> actual = NKLandscapeWalshTransformer.extractCoefficients(nk);

        int size = 1 << n;
        for (int mask = 0; mask < size; mask++) {
            double value = actual.getOrDefault((long) mask, 0.0);
            assertEquals(expected[mask], value, 1e-10);
        }

        int localSize = 1 << (k + 1);
        assertTrue(actual.size() <= n * localSize);
    }
}
