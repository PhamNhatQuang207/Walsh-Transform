# APPENDIX: User Manual — FWHT on Black-Box Functions and NK-Landscapes

---

## 📖 Table of Contents
1. [What is the Walsh Transform & FWHT?](#1-what-is-the-walsh-transform--fwht)
2. [Build the Project](#2-build-the-project)
3. [Use Case 1 — Applying FWHT to a Black-Box Function](#3-use-case-1--applying-fwht-to-a-black-box-function)
4. [Use Case 2 — Applying FWHT to an NK-Landscape](#4-use-case-2--applying-fwht-to-an-nk-landscape)
5. [Visualizing Results with Jupyter Notebooks](#5-visualizing-results-with-jupyter-notebooks)
   * [5.1 Setup](#51-setup)
   * [5.2 Notebook 1: Single Landscape Coefficient Distribution](#52-notebook-1-single-landscape-coefficient-distribution)
   * [5.3 Notebook 2: Comparative Analysis (Varying $n$ and $k$)](#53-notebook-2-comparative-analysis-varying-n-and-k)
   * [5.4 Notebook 3: Deep Epistasis Inspection](#54-notebook-3-deep-epistasis-inspection)

---

## 1. What is the Walsh Transform & FWHT?

The **Walsh Transform** decomposes a function over binary inputs $f: \{0, 1\}^n \to \mathbb{R}$ into its Walsh spectrum — a set of coefficients $w_j$ that reveal how much each group of interacting variables (of any order) contributes to the function's output:

$$f(x) = \sum_{j=0}^{2^n-1} w_j \cdot \psi_j(x), \quad \text{where} \quad \psi_j(x) = (-1)^{\sum_i j_i x_i}$$

The **Fast Walsh-Hadamard Transform (FWHT)** computes the full Walsh spectrum in-place with $O(n \cdot 2^n)$ time and $O(2^n)$ memory, by exploiting the recursive butterfly structure of the Hadamard matrix. This is the key algorithm that makes analysis of large-scale black-box functions and NK-Landscapes tractable.

---

## 2. Build the Project

The project uses Maven. Run the following once before using any program:

```bash
mvn package
```

This compiles all Java sources and produces `target/my-lib-0.1.0.jar`.

---

## 3. Use Case 1 — Applying FWHT to a Black-Box Function

**Program**: `Main.java`

This program demonstrates the full FWHT pipeline on a randomly generated black-box function:
1. Generates a random function $f: \{0,1\}^n \to [0, Q]$.
2. Applies FWHT to compute all $2^n$ Walsh coefficients.
3. Reconstructs $f(x)$ from the Walsh polynomial to verify correctness.

**Run:**
```bash
mvn exec:java
```

**Interactive prompts:**
```
Input dimension (n): 4
Input maximum value (Q): 10
```

- `n` — number of binary input bits. The function has $2^n$ possible inputs. Keep `n` small (e.g., 4–12) for this interactive demo since it enumerates all $2^n$ states.
- `Q` — maximum output value of the random function.

**Sample output:**
```
---  Input/Output of Black-box ---
Input (x)       | Output f(x)
------------------------------------------
[false, false, false, false] | 7.2341
[false, false, false, true]  | 3.1042
...

--- Start calculating 16 Walsh coefficients ---

Index k    | Matrix Method        | Summation Method     | Status
----------------------------------------------------------------------------------
0          | 4.9876543210         | 4.9876543210         | MATCH
1          | 0.3214567890         | 0.3214567890         | MATCH
...

--- Reconstruction verification using WalshPolynomial ---
Input (x)       | Original f(x)  | Reconstructed  | Status
----------------------------------------------------------------------------
[false, ...]    | 7.2341         | 7.2341         | MATCH
...
```

**What to look for in the output:**
- The **Walsh coefficients** (column `Matrix Method`) represent the contribution of each variable interaction. Coefficient at index 0 is the mean of $f$.
- **Reconstruction MATCH** confirms the Walsh polynomial exactly represents $f$.

---

## 4. Use Case 2 — Applying FWHT to an NK-Landscape

**Program**: `NKLandscapeFwhtRunner.java`

An **NK-Landscape** is a canonical fitness landscape model where each of the $n$ variables interacts with $k$ other variables:

$$F(x) = \frac{1}{n} \sum_{i=1}^n f_i(x_i, x_{i_1}, \dots, x_{i_k})$$

Because $n$ can be in the hundreds or thousands, the FWHT is applied in a **sparse bitmask mode** — it only computes and stores the non-zero Walsh coefficients (since the maximum interaction order is bounded by $k+1$), making it memory-efficient even for large landscapes.

The program writes all extracted coefficients to a **CSV file** for downstream analysis.

### NK-Landscape Class Hierarchy & Initialization

The library separates the NK fitness landscape into a general representation and a random subclass:
1. **`NKLandscape`** — The general class. It represents an NK landscape with custom variable interactions and local fitness contribution tables. This allows you to model custom system architectures.
2. **`RandomNKLandscape`** — A subclass of `NKLandscape` that automatically generates random interacting variables and populates the contribution tables with random values in $[0, 1)$.

#### Custom/General NKLandscape Initialization
To instantiate an NK-landscape with pre-defined structures:
```java
int n = 3; // number of variables
int k = 1; // number of interactions per variable

// Interactions: for each variable, listing itself and its k interacting neighbors
int[][] interactions = {
    {0, 1},
    {1, 2},
    {2, 0}
};

// Fitness tables: for each variable, listing 2^(k+1) configuration values
double[][][] interactionTables = {
    {{0.1}, {0.2}, {0.3}, {0.4}},
    {{0.5}, {0.6}, {0.7}, {0.8}},
    {{0.9}, {0.1}, {0.2}, {0.3}}
};

NKLandscape landscape = new NKLandscape(n, k, interactions, interactionTables);
```

#### Random NKLandscape Initialization
To instantiate a random NK-landscape:
```java
// Automatically generates random interactions and contribution tables
NKLandscape landscape = new RandomNKLandscape(n, k);

// Or with a seed for reproducibility:
NKLandscape landscape = new RandomNKLandscape(n, k, 42L);
```

**Run (interactive mode):**
```bash
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner
```

```
Input dimension (n): 1000
Input interactions (k): 3
Output CSV path (press Enter for default nk_walsh_coefficients.csv):
```

**Or run with command-line arguments:**
```bash
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner 1000 3 statistic/n1000_k3.csv
```

**Parameters:**
| Parameter | Description | Example |
|-----------|-------------|---------|
| `n` | Number of binary variables in the landscape | `1000` |
| `k` | Number of epistatic neighbors per variable ($k < n$) | `3` |
| `outputPath` | Path for the output CSV file | `statistic/n1000_k3.csv` |

**Output CSV format** (`order, indices, coefficient`):
```
order,indices,coefficient
0,,0.498435
1,602,-0.000611
1,499,0.000605
2,3;47,-0.000312
...
```

- **`order`** — the number of variables in this interaction (0 = global mean, 1 = single-variable effect, 2 = pairwise interaction, etc.)
- **`indices`** — the specific variable indices involved in this Walsh term (semicolon-separated)
- **`coefficient`** — the strength of that interaction

> [!NOTE]
> For a landscape with $k=3$, the maximum order of any non-zero coefficient is $k+1 = 4$. Higher-order interactions are structurally zero and are never stored.

**Recommended datasets to generate for analysis:**
```bash
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner 1000 2 statistic/n1000_k2.csv
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner 1000 3 statistic/n1000_k3.csv
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner 1000 4 statistic/n1000_k4.csv
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner 500  3 statistic/n500_k3.csv
```

---

## 5. Visualizing Results with Jupyter Notebooks

The `/statistic/` directory contains three Jupyter Notebooks that load the CSV files produced by `NKLandscapeFwhtRunner` and generate statistical plots.

### 5.1 Setup

Install Python dependencies (one time):
```bash
pip install pandas numpy matplotlib seaborn jupyter
```

Launch the Jupyter environment from the `statistic/` folder:
```bash
cd statistic
jupyter notebook
```

A browser window will open listing all notebooks.

---

### 5.2 Notebook 1: Single Landscape Coefficient Distribution

**File:** `walsh_coefficients_visualization.ipynb`

**What it does:** Visualizes the full distribution of Walsh coefficients for one landscape run.

**Steps:**
1. Open the notebook and find the first code cell. Set the CSV path to one of your generated files:
   ```python
   csv_path = "./n1000_k2.csv"
   ```
2. Run all cells: **Kernel → Restart & Run All**

**Plots generated:**

| Section | Plot | What it shows |
|---------|------|---------------|
| Section 2 | Coefficient histogram (symlog scale) | The full distribution of $w_j$ values — most are near zero, with a tall central spike |
| Section 2 | Absolute coefficient histogram (log scale) | The magnitudes $abs(w_j)$ — useful to see the decay across scale |
| Section 3 | Per-order distributions (log scale) | How coefficient magnitudes are distributed within each interaction order |
| Section 4 | Per-order distributions (linear scale) | Same view with a linear x-axis for shape clarity |

---

### 5.3 Notebook 2: Comparative Analysis (Varying $n$ and $k$)

**File:** `nk_landscape_comparison_analysis.ipynb`

**What it does:** Compares Walsh spectra across multiple runs to show how changing $n$ (landscape size) or $k$ (epistasis degree) affects the structure of the landscape.

**Steps:**
1. Make sure the four CSV files are in the `statistic/` folder:
   - `n1000_k2.csv`, `n1000_k3.csv`, `n1000_k4.csv`, `n500_k3.csv`
2. Open the notebook and run all cells: **Kernel → Restart & Run All**

**Plots generated:**

| Plot | What it shows |
|------|---------------|
| Effect of $k$ — non-zero coefficient count | Increasing $k$ from 3→4 increases the number of active Walsh terms significantly (landscape becomes more rugged) |
| Effect of $k$ — mean coefficient magnitude | As more interaction terms appear, the energy is distributed among them, so each term's mean magnitude decreases |
| Effect of $n$ — non-zero coefficient count | Doubling $n$ doubles the number of active terms (linear scaling, because the number of local neighborhoods scales with $n$) |
| Effect of $n$ — mean coefficient magnitude | Larger $n$ dilutes each term's contribution, since fitness is averaged over more variables |
| Per-order line chart (varying $k$) | The $k+1$ hard cutoff on interaction order shifts as $k$ changes |
| Per-order line chart (varying $n$) | All order counts scale proportionally with $n$, keeping the shape identical |

---

### 5.4 Notebook 3: Deep Epistasis Inspection

**File:** `walsh_coefficients_visualize_advance.ipynb`

**What it does:** Lets you inspect which specific variable combinations are driving the strongest interactions in the landscape.

**Steps:**
1. Open the notebook, set the CSV path in the first cell.
2. Run all cells: **Kernel → Restart & Run All**

**What you can explore:**
- The top-N largest Walsh coefficients and which exact variable indices they involve.
- Confirmation that no coefficient of order $> k+1$ is ever non-zero.
- Advanced visualizations of the Walsh spectrum organized by variable interaction patterns.
