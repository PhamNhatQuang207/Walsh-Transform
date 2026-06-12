import unittest
import math
from walshtransform import (
    BlackBoxFunction,
    FastWalshTransformer,
    NKLandscape,
    RandomNKLandscape,
    NKLandscapeWalshTransformer
)

class SimpleMaxOneFunction(BlackBoxFunction):
    """
    Simple function where f(x) = sum(x_i)
    """
    def evaluate(self, x):
        return float(sum(1 if val else 0 for val in x))


class TestWalshTransform(unittest.TestCase):

    def test_black_box_dimension(self):
        f = SimpleMaxOneFunction(5)
        self.assertEqual(f.dimension, 5)

    def test_fwht_invalid_size(self):
        with self.assertRaises(ValueError):
            FastWalshTransformer.fwht([1.0, 2.0, 3.0])  # Not a power of two
        with self.assertRaises(ValueError):
            FastWalshTransformer.fwht([])  # Empty list

    def test_fwht_small_example(self):
        # f(x) = [1.0, 2.0, 3.0, 4.0]
        a = [1.0, 2.0, 3.0, 4.0]
        FastWalshTransformer.fwht(a)
        # Expected:
        # Step 1 (len=1):
        # i=0: a[0]=1+2=3, a[1]=1-2=-1
        # i=2: a[2]=3+4=7, a[3]=3-4=-1
        # a = [3, -1, 7, -1]
        # Step 2 (len=2):
        # i=0:
        # j=0: a[0]=3+7=10, a[2]=3-7=-4
        # j=1: a[1]=-1+(-1)=-2, a[3]=-1-(-1)=0
        # a = [10, -2, -4, 0]
        self.assertAlmostEqual(a[0], 10.0)
        self.assertAlmostEqual(a[1], -2.0)
        self.assertAlmostEqual(a[2], -4.0)
        self.assertAlmostEqual(a[3], 0.0)

    def test_calculate_walsh_coefficients(self):
        f = SimpleMaxOneFunction(3)
        coefs = FastWalshTransformer.calculate_walsh_coefficients(f)
        # Since n=3, 2^3 = 8 coefficients
        self.assertEqual(len(coefs), 8)
        
        # We can reconstruct f(x) and verify it matches original evaluations
        for i in range(8):
            x = [bool((i >> bit) & 1) for bit in range(3)]
            original = f.evaluate(x)
            
            # Reconstruction: f(x) = sum_{j} w_j * (-1)^{sum_bit j_bit * x_bit}
            reconstructed = 0.0
            for j in range(8):
                parity = 0
                for bit in range(3):
                    if ((j >> bit) & 1) and x[bit]:
                        parity += 1
                sign = -1.0 if parity % 2 == 1 else 1.0
                reconstructed += coefs[j] * sign
                
            self.assertAlmostEqual(original, reconstructed, places=9)

    def test_nk_landscape_validation(self):
        with self.assertRaises(ValueError):
            RandomNKLandscape(3, 3)  # k must be < n
        with self.assertRaises(ValueError):
            RandomNKLandscape(3, 4)  # k must be < n
        with self.assertRaises(ValueError):
            RandomNKLandscape(4, -1) # k must be non-negative

        # Valid constructor
        nk = RandomNKLandscape(4, 2, seed=123)
        self.assertEqual(nk.dimension, 4)
        self.assertEqual(nk.k, 2)

    def test_custom_nk_landscape_evaluation(self):
        n = 3
        k = 1
        interactions = [
            [0, 1],
            [1, 2],
            [2, 0]
        ]
        tables = [
            [[0.1], [0.2], [0.3], [0.4]],
            [[0.5], [0.6], [0.7], [0.8]],
            [[0.9], [0.15], [0.25], [0.35]]
        ]
        
        nk = NKLandscape(n, k, interactions, tables)
        
        # Test defensive copy: modify source lists and make sure nk is not changed
        interactions[0][1] = 99
        tables[0][0][0] = 99.9
        
        self.assertEqual(nk.interactions[0][1], 1)
        self.assertAlmostEqual(nk.interaction_tables[0][0][0], 0.1)
        
        # Test evaluation for x = [False, False, False]
        # i=0: indices [0, 1] => x[0]=F, x[1]=F => table index 0 => 0.1
        # i=1: indices [1, 2] => x[1]=F, x[2]=F => table index 0 => 0.5
        # i=2: indices [2, 0] => x[2]=F, x[0]=F => table index 0 => 0.9
        # Sum = 1.5, Average = 0.5
        self.assertAlmostEqual(nk.evaluate([False, False, False]), 0.5)

        # Test evaluation for x = [True, False, False]
        # i=0: indices [0, 1] => x[0]=T, x[1]=F => table index 1 => 0.2
        # i=1: indices [1, 2] => x[1]=F, x[2]=F => table index 0 => 0.5
        # i=2: indices [2, 0] => x[2]=F, x[0]=T => table index 2 => 0.25
        # Sum = 0.95, Average = 0.95 / 3 = 0.31666666666...
        self.assertAlmostEqual(nk.evaluate([True, False, False]), 0.95 / 3.0)

    def test_nk_transformer_equivalence(self):
        # Generate a small random NK-Landscape
        n = 5
        k = 2
        nk = RandomNKLandscape(n, k, seed=42)
        
        # 1. Compute full Walsh coefficients using FastWalshTransformer
        full_coefs = FastWalshTransformer.calculate_walsh_coefficients(nk)
        
        # 2. Extract sparse coefficients using NKLandscapeWalshTransformer
        sparse_coefs = NKLandscapeWalshTransformer.extract_coefficients(nk)
        
        # Compare them
        for mask in range(1 << n):
            # Convert mask to frozenset of active variable indices
            active_vars = []
            for bit in range(n):
                if (mask >> bit) & 1:
                    active_vars.append(bit)
            key = frozenset(active_vars)
            
            expected_val = full_coefs[mask]
            actual_val = sparse_coefs.get(key, 0.0)
            
            self.assertAlmostEqual(expected_val, actual_val, places=10)

if __name__ == '__main__':
    unittest.main()
