import random
from typing import List, Optional
from .nk_landscape import NKLandscape

class RandomNKLandscape(NKLandscape):
    """
    Subclass of NKLandscape that auto-initializes interactions and tables randomly.
    """
    def __init__(self, n: int, k: int, seed: Optional[int] = None):
        if k < 0:
            raise ValueError("k must be non-negative")
        if k >= n:
            raise ValueError("k must be less than n")
            
        rng = random.Random(seed) if seed is not None else random.Random()
        
        def row_contains(arr, val, limit):
            for idx in range(limit):
                if arr[idx] == val:
                    return True
            return False

        interactions = []
        for i in range(n):
            row = [0] * (k + 1)
            row[0] = i
            for j in range(1, k + 1):
                while True:
                    neighbor = rng.randint(0, n - 1)
                    if not row_contains(row, neighbor, j):
                        row[j] = neighbor
                        break
            interactions.append(row)
            
        num_configs = 1 << (k + 1)
        interaction_tables = []
        for i in range(n):
            table = [[rng.random()] for _ in range(num_configs)]
            interaction_tables.append(table)
            
        super().__init__(n, k, interactions, interaction_tables)
