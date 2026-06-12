from abc import ABC, abstractmethod
from typing import List

class BlackBoxFunction(ABC):
    """
    Abstract base class for black-box functions over binary vectors.
    """
    def __init__(self, n: int):
        if n < 0:
            raise ValueError("dimension n must be non-negative")
        self._n = n

    @abstractmethod
    def evaluate(self, x: List[bool]) -> float:
        """
        Evaluates the black-box function at a given point in the search space.
        :param x: list of booleans representing the input vector.
        :return: the real value of the black-box function.
        """
        pass

    @property
    def dimension(self) -> int:
        return self._n
