from typing import List
from .black_box_function import BlackBoxFunction

class NKLandscape(BlackBoxFunction):
    """
    Implements an NK fitness landscape as a black-box function over binary vectors.
    """
    def __init__(self, n: int, k: int, interactions: List[List[int]], interaction_tables: List[List[List[float]]]):
        super().__init__(n)
        self._k = k
        
        if n <= 0:
            raise ValueError("n must be greater than 0")
        if k < 0:
            raise ValueError("k must be non-negative")
        if k >= n:
            raise ValueError("k must be less than n")
        if interactions is None or interaction_tables is None:
            raise ValueError("interactions and interaction tables must not be None")
        if len(interactions) != n:
            raise ValueError("interactions length must equal n")
        if len(interaction_tables) != n:
            raise ValueError("interaction_tables length must equal n")
            
        num_configs = 1 << (k + 1)
        for i in range(n):
            if interactions[i] is None or len(interactions[i]) != k + 1:
                raise ValueError(f"interactions[{i}] must have length {k + 1}")
            if interaction_tables[i] is None or len(interaction_tables[i]) != num_configs:
                raise ValueError(f"interaction_tables[{i}] must have length {num_configs}")
            for j in range(num_configs):
                if interaction_tables[i][j] is None or len(interaction_tables[i][j]) != 1:
                    raise ValueError(f"interaction_tables[{i}][{j}] must have length 1")
                    
        # Defensive deep copy
        self._interactions = [list(row) for row in interactions]
        self._interaction_tables = [[[val[0]] for val in row] for row in interaction_tables]

    def evaluate(self, x: List[bool]) -> float:
        if x is None:
            raise ValueError("x must not be None")
        if len(x) != self._n:
            raise ValueError(f"x must have length {self._n}")
            
        total_fitness = 0.0
        for i in range(self._n):
            table_index = 0
            for j in range(self._k + 1):
                if x[self._interactions[i][j]]:
                    table_index += 1 << j
            total_fitness += self._interaction_tables[i][table_index][0]
        return total_fitness / self._n

    def describe_properties(self) -> str:
        lines = [
            "NK Landscape properties",
            f"n={self._n}, k={self._k}",
            "Interactions:"
        ]
        for i in range(self._n):
            lines.append(f"  i={i} -> {self._interactions[i]}")
        lines.append("Contribution tables:")
        for i in range(self._n):
            for j in range(len(self._interaction_tables[i])):
                lines.append(f"  i={i}, idx={j} => {self._interaction_tables[i][j][0]}")
        return "\n".join(lines) + "\n"

    @property
    def k(self) -> int:
        return self._k

    @property
    def interactions(self) -> List[List[int]]:
        return [list(row) for row in self._interactions]

    @property
    def interaction_tables(self) -> List[List[List[float]]]:
        return [[[val[0]] for val in row] for row in self._interaction_tables]
