# User Manual — Walsh Transform & NK-Landscapes (Python Version)

This guide walks you through using the Python implementation of the **Walsh-Hadamard Transform** library. It contains a modular package (`walshtransform`) for analyzing black-box functions and extracting sparse Walsh coefficients from NK-Landscapes.

---

## 📖 Table of Contents
1. [Setup and Installation](#1-setup-and-installation)
2. [Use Case 1 — Applying FWHT to a Custom Black-Box Function](#2-use-case-1--applying-fwht-to-a-custom-black-box-function)
3. [Use Case 2 — Extracting Coefficients from a Random NK-Landscape](#3-use-case-2--extracting-coefficients-from-a-random-nk-landscape)
4. [Use Case 3 — Building a Custom/General NK-Landscape](#4-use-case-3--building-a-customgeneral-nk-landscape)
5. [Running Unit Tests](#5-running-unit-tests)

---

## 1. Setup and Installation

### Prerequisites
* Python 3.8+

### Project Structure
The Python library is organized as a modular package under the `python/walshtransform/` directory:
```
python/
  walshtransform/
    __init__.py
    black_box_function.py
    fast_walsh_transformer.py
    nk_landscape.py
    random_nk_landscape.py
    nk_landscape_walsh_transformer.py
  test_walshtransform.py
```

### Importing the Package
To import the library classes in your Python scripts, ensure the `python/` directory is in your Python path:
```python
import sys
# If running a script outside the python/ directory:
sys.path.append("/path/to/Walsh-Transform/python")

from walshtransform import (
    BlackBoxFunction,
    FastWalshTransformer,
    NKLandscape,
    RandomNKLandscape,
    NKLandscapeWalshTransformer
)
```

---

## 2. Use Case 1 — Applying FWHT to a Custom Black-Box Function

To analyze any binary black-box function, subclass `BlackBoxFunction` and implement the `evaluate` method.

### Example Code
```python
from typing import List
from walshtransform import BlackBoxFunction, FastWalshTransformer

class MyOneMaxFunction(BlackBoxFunction):
    """
    Computes fitness as the sum of True values (OneMax problem).
    """
    def evaluate(self, x: List[bool]) -> float:
        return float(sum(1 if val else 0 for val in x))

# 1. Initialize function with 4 binary variables
f = MyOneMaxFunction(n=4)

# 2. Compute all 2^4 = 16 Walsh coefficients
coefficients = FastWalshTransformer.calculate_walsh_coefficients(f)

# 3. Print non-zero coefficients
print("--- Walsh Spectrum ---")
for idx, coef in enumerate(coefficients):
    if abs(coef) > 1e-10:
        # Convert index to a set of active variables
        active_vars = [bit for bit in range(f.dimension) if (idx >> bit) & 1]
        print(f"Index {idx} (Variables {active_vars}) -> Coefficient: {coef:.4f}")
```

---

## 3. Use Case 2 — Extracting Coefficients from a Random NK-Landscape

NK-Landscapes can scale to large dimensions (e.g., $n=1000$) by exploiting interaction sparsity to extract only the non-zero Walsh coefficients.

### Example Code
```python
from walshtransform import RandomNKLandscape, NKLandscapeWalshTransformer

# 1. Create a random NK-Landscape with:
#    n = 1000 variables
#    k = 3 interacting variables per variable
#    seed = 42 for reproducibility
landscape = RandomNKLandscape(n=1000, k=3, seed=42)

# 2. Extract sparse coefficients (returns a dictionary: frozenset -> coefficient)
coefficients = NKLandscapeWalshTransformer.extract_coefficients(landscape)

# 3. Print the first 10 non-zero coefficients
print("--- Sparse NK Landscape Walsh Coefficients ---")
for active_vars, coef in list(coefficients.items())[:10]:
    order = len(active_vars)
    variables = sorted(list(active_vars))
    print(f"Order: {order}, Variables: {variables} -> Coefficient: {coef:.6f}")
```

---

## 4. Use Case 3 — Building a Custom/General NK-Landscape

If you have specific variable interactions and local contribution tables, you can construct a general `NKLandscape` class manually.

### Example Code
```python
from walshtransform import NKLandscape

# 3 variables, 1 interaction per variable (k = 1)
n = 3
k = 1

# List of interacting variables for each variable index i
# interactions[i][0] must typically be variable i itself
interactions = [
    [0, 1], # Variable 0 interacts with 1
    [1, 2], # Variable 1 interacts with 2
    [2, 0]  # Variable 2 interacts with 0
]

# Local contribution tables (shape: n x 2^(k+1) x 1)
# For k=1, there are 2^(1+1) = 4 local configurations per variable
interaction_tables = [
    [[0.1], [0.2], [0.3], [0.4]], # Local table for variable 0
    [[0.5], [0.6], [0.7], [0.8]], # Local table for variable 1
    [[0.9], [0.1], [0.2], [0.3]]  # Local table for variable 2
]

# Construct the custom landscape
custom_landscape = NKLandscape(n, k, interactions, interaction_tables)

# Evaluate on a custom input
x = [True, False, False]
fitness = custom_landscape.evaluate(x)
print(f"Fitness of {x}: {fitness:.4f}")
```

---

## 5. Running Unit Tests

The test suite checks FWHT mathematics, NK evaluation, parameter validation, defensive copying, and verifies equivalence between full FWHT and sparse extraction.

Navigate to the `python/` directory and run:
```bash
python3 test_walshtransform.py
```

### Expected Output
```
.......
----------------------------------------------------------------------
Ran 7 tests in 0.001s

OK
```
