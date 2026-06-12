# Walsh Transform Java Library

A Java library for computing the **Fast Walsh-Hadamard Transform (FWHT)** on black-box functions and NK-Landscape fitness models.

## Using as a Library in Your Project

### Option 1: GitHub Packages (recommended)

Add to your `~/.m2/settings.xml` (create it if it doesn't exist) to authenticate with GitHub Packages:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

> Generate a token at **GitHub → Settings → Developer Settings → Personal Access Tokens** with the `read:packages` scope.

Then add this to your project's `pom.xml`:

```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/PhamNhatQuang207/Walsh-Transform</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.walshtransform</groupId>
    <artifactId>walsh-transform</artifactId>
    <version>0.1.0</version>
  </dependency>
</dependencies>
```

### Option 2: Local JAR

Clone the repository, build the JAR, and install it to your local Maven cache:

```bash
git clone https://github.com/PhamNhatQuang207/Walsh-Transform.git
cd Walsh-Transform
mvn install
```

Then add the dependency in your `pom.xml`:

```xml
<dependency>
  <groupId>com.walshtransform</groupId>
  <artifactId>walsh-transform</artifactId>
  <version>0.1.0</version>
</dependency>
```

---

## API: Applying FWHT to Your Own Black-Box Function

### Step 1 — Implement your function

Extend `BlackBoxFunction` and override `evaluate`:

```java
import com.walshtransform.BlackBoxFunction;

public class MyFunction extends BlackBoxFunction {

    public MyFunction(int n) {
        super(n);  // n = number of binary variables
    }

    @Override
    public double evaluate(boolean[] x) {
        // Return f(x) — your fitness, cost, or reward for input x
        double value = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i]) value += (i + 1);
        }
        return value;
    }
}
```

### Step 2 — Run FWHT and read the Walsh coefficients

```java
import com.walshtransform.FastWalshTransformer;

public class Main {
    public static void main(String[] args) {
        int n = 5;  // 2^5 = 32 possible inputs — keep n small (≤ 20) for full FWHT
        MyFunction f = new MyFunction(n);

        // Compute all 2^n Walsh coefficients
        double[] coefficients = FastWalshTransformer.calculateWalshCoefficients(f);

        // coefficients[0]  → global mean of f
        // coefficients[j]  → strength of the interaction defined by the bits of j
        for (int j = 0; j < coefficients.length; j++) {
            if (Math.abs(coefficients[j]) > 1e-10) {
                System.out.printf("w[%d] = %.6f%n", j, coefficients[j]);
            }
        }
    }
}
```

> **Memory note**: `FastWalshTransformer` allocates a `double[2^n]` array. Keep `n ≤ 20` for typical machines. For larger `n`, use the NK-Landscape sparse extractor below.

---

## API: Applying FWHT to an NK-Landscape

NK-Landscapes support arbitrarily large `n` by exploiting the sparse structure of interactions — only the non-zero coefficients are computed and stored.

### Option A: Using a Random NK-Landscape
To generate a random landscape (where variable interactions and contribution tables are automatically created at random), use `RandomNKLandscape`:

```java
import com.walshtransform.NKLandscape;
import com.walshtransform.RandomNKLandscape;
import com.walshtransform.BigNKLandscapeWalshTransformer;
import java.util.BitSet;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        int n = 1000;  // large landscape — no memory issue
        int k = 3;     // each variable interacts with 3 others

        NKLandscape landscape = new RandomNKLandscape(n, k);

        // Extract sparse Walsh coefficients (only non-zero terms are returned)
        Map<BitSet, Double> coefficients = BigNKLandscapeWalshTransformer.extractCoefficients(landscape);

        // Each entry: BitSet (which variables interact) → coefficient value
        for (Map.Entry<BitSet, Double> entry : coefficients.entrySet()) {
            int order = entry.getKey().cardinality();  // number of variables involved
            double value = entry.getValue();
            System.out.printf("order=%d, vars=%s, w=%.6f%n", order, entry.getKey(), value);
        }
    }
}
```

### Option B: Using a Custom/General NK-Landscape
If you have a specific system/function where variable interactions and local fitness tables are already known, you can construct a general `NKLandscape` by providing them:

```java
// Interactions table of size n x (k+1):
// For each variable i, list itself (typically at index 0) and its k interacting variables.
int[][] interactions = {
    {0, 1}, // variable 0 interacts with variable 1
    {1, 2}, // variable 1 interacts with variable 2
    {2, 0}  // variable 2 interacts with variable 0
};

// Fitness tables of size n x 2^(k+1) x 1:
// For each variable i, the table contains local fitness values for all 2^(k+1) input configurations.
double[][][] interactionTables = {
    {{0.1}, {0.2}, {0.3}, {0.4}}, // contributions for variable 0
    {{0.5}, {0.6}, {0.7}, {0.8}}, // contributions for variable 1
    {{0.9}, {0.1}, {0.2}, {0.3}}  // contributions for variable 2
};

NKLandscape landscape = new NKLandscape(3, 1, interactions, interactionTables);
```

**Key properties:**
- For a landscape with epistasis `k`, all Walsh coefficients of order `> k+1` are exactly zero — the library never computes or stores them.
- The returned `Map` is keyed by `BitSet`, where each set bit is the index of a variable participating in that interaction.

---

## Requirements

- JDK 17+
- Maven 3.8+

## Build and Test

```bash
mvn clean test   # run the full test suite
mvn package      # build the JAR to target/walsh-transform-0.1.0.jar
```

## Running the Interactive Demo

```bash
mvn exec:java    # prompts for n and Q, then prints all Walsh coefficients
```

## Running the NK-Landscape Extractor (CLI)

```bash
mvn package
java -cp target/classes com.walshtransform.NKLandscapeFwhtRunner 1000 3 output.csv
```

Writes a CSV with columns `order,indices,coefficient`. Use the Jupyter notebooks in `/statistic/` to visualize the results.

## Visualizing Results

See the Jupyter notebooks in [`/statistic/`](statistic/):

| Notebook | Purpose |
|----------|---------|
| `walsh_coefficients_visualization.ipynb` | Histogram and per-order distribution of coefficients for a single run |
| `nk_landscape_comparison_analysis.ipynb` | Compare how varying `n` and `k` affects the Walsh spectrum |
| `mean_density_analysis` | Calculate mean error for many instances |

```bash
cd statistic
pip install pandas numpy matplotlib seaborn jupyter
jupyter notebook
```
