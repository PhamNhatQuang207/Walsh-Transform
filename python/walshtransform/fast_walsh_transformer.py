from typing import List
from .black_box_function import BlackBoxFunction

class FastWalshTransformer:
    """
    Computes the Fast Walsh-Hadamard Transform (FWHT) on black-box functions.
    """
    @staticmethod
    def fwht(a: List[float]) -> None:
        """
        Performs the Fast Walsh-Hadamard Transform in-place.
        """
        n = len(a)
        if n == 0 or (n & (n - 1)) != 0:
            raise ValueError("array length must be a power of two")
        
        length = 1
        while 2 * length <= n:
            for i in range(0, n, 2 * length):
                for j in range(length):
                    u = a[i + j]
                    v = a[i + length + j]
                    a[i + j] = u + v
                    a[i + length + j] = u - v
            length *= 2

    @staticmethod
    def calculate_walsh_coefficients(f: BlackBoxFunction) -> List[float]:
        """
        Computes all 2^n Walsh coefficients for a given black-box function.
        """
        n = f.dimension
        total_solutions = 1 << n
        fitness_values = []
        for i in range(total_solutions):
            binary_vector = [bool((i >> bit) & 1) for bit in range(n)]
            fitness_values.append(f.evaluate(binary_vector))
            
        FastWalshTransformer.fwht(fitness_values)
        
        scale = 1.0 / total_solutions
        for i in range(total_solutions):
            fitness_values[i] *= scale
            
        return fitness_values
