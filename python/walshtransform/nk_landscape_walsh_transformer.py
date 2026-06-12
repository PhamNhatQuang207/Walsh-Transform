from typing import Dict, FrozenSet
from .nk_landscape import NKLandscape
from .fast_walsh_transformer import FastWalshTransformer

class NKLandscapeWalshTransformer:
    """
    Grey-box Walsh coefficient extractor for NK landscapes exploiting sparsity.
    """
    @staticmethod
    def extract_coefficients(nk: NKLandscape) -> Dict[FrozenSet[int], float]:
        if nk is None:
            raise ValueError("nk must not be None")
            
        n = nk.dimension
        k = nk.k
        local_size = 1 << (k + 1)
        tables = nk.interaction_tables
        interactions = nk.interactions
        
        coefficients = {}
        local_scale = 1.0 / local_size
        
        for i in range(n):
            # Copy local contribution table
            local = [tables[i][idx][0] for idx in range(local_size)]
            
            # Compute FWHT in-place on local contribution table
            FastWalshTransformer.fwht(local)
            
            for idx in range(local_size):
                value = local[idx] * local_scale
                if abs(value) < 1e-12:
                    continue
                    
                # Map local mask bits to their global variable positions
                global_indices = []
                local_mask = idx
                for bit in range(k + 1):
                    if (local_mask & 1) != 0:
                        global_indices.append(interactions[i][bit])
                    local_mask >>= 1
                    
                key = frozenset(global_indices)
                coefficients[key] = coefficients.get(key, 0.0) + value
                
        # Scale by 1/n
        global_scale = 1.0 / n
        for key in list(coefficients.keys()):
            coefficients[key] *= global_scale
            
        return coefficients
