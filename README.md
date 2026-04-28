# Walsh Transform Java Library

Java library project built with Maven and Java 17 for calculating Walsh transforms.

## Features

- **HadamardMatrix**: generates Sylvester-type Hadamard matrices of order `2^n`.
- **WalshTransformer**: computes Walsh coefficients using the Hadamard matrix method.
- **WalshTransformerDirect**: computes Walsh coefficients using the direct summation method.
- **FastWalshTransformer**: computes Walsh coefficients using the Fast Walsh-Hadamard Transform (FWHT).
- **BlackBoxFunction**: abstract base class and random implementation for black-box function evaluation over integer vectors.
- **Main**: Interactive CLI application to test and compare both Walsh transform methods.
- **WalshTransformBenchmark**: benchmark runner that averages matrix and FWHT timing across dimensions and outputs CSV and PNG chart.
- **WalshTransformFwhtCheck**: CLI checker to compare FWHT and matrix results with a per-index table.
- JUnit 5 tests to verify the correctness of the matrix generation and transformations.

## Project Structure

- `src/main/java/com/walshtransform/HadamardMatrix.java`
- `src/main/java/com/walshtransform/BlackBoxFunction.java`
- `src/main/java/com/walshtransform/RandomBlackBox.java`
- `src/main/java/com/walshtransform/WalshTransformer.java`
- `src/main/java/com/walshtransform/WalshTransformerDirect.java`
- `src/main/java/com/walshtransform/FastWalshTransformer.java`
- `src/main/java/com/walshtransform/BinaryVectorUtils.java`
- `src/main/java/com/walshtransform/Main.java`
- `src/main/java/com/walshtransform/WalshTransformBenchmark.java`
- `src/main/java/com/walshtransform/WalshTransformFwhtCheck.java`

## Requirements

- JDK 17+
- Maven 3.8+

## Build and Test

To compile the project and run the test suite:

```bash
mvn clean test
```

To package the project into a JAR file:

```bash
mvn package
```

## Running the Application

You can easily run the interactive CLI application to compare the performance and outputs of both Walsh transform methods using the following Maven command:

```bash
mvn exec:java
```

When you run this command, it will prompt you for the input dimension `n` and a maximum value `Q` for the random black-box function.

## Running the Benchmark

Build the project and run the benchmark directly:

```bash
mvn -q -DskipTests package
java -cp target/classes com.walshtransform.WalshTransformBenchmark
```

You can also pass arguments:

```bash
java -cp target/classes com.walshtransform.WalshTransformBenchmark nStart nEnd Q [samples] [csvPath] [chartPath]
```

Example:

```bash
java -cp target/classes com.walshtransform.WalshTransformBenchmark 2 10 1.0 5 results.csv results.png
```

The benchmark writes a CSV file and a PNG line chart to visualize how average time grows with dimension.

The CSV columns are:

- `dimension`
- `states`
- `avg_matrix_ms`
- `avg_fwht_ms`

## Comparing FWHT vs Matrix

Build the project and run the FWHT comparison CLI:

```bash
mvn -q -DskipTests package
java -cp target/classes com.walshtransform.WalshTransformFwhtCheck
```

You can also pass arguments:

```bash
java -cp target/classes com.walshtransform.WalshTransformFwhtCheck n Q [epsilon]
```

Example:

```bash
java -cp target/classes com.walshtransform.WalshTransformFwhtCheck 6 1.0 1e-12
```

## Notes

- `HadamardMatrix.generate(n)` uses recursion with base case `H(0) = [1]`.
- Input `n` must be non-negative; invalid values throw `IllegalArgumentException`.
- Matrix values are stored as `int` (`+1` and `-1`).
- The matrix method allocates the full Hadamard matrix in memory. Large dimensions (for example, `n >= 15`) can require several GB of heap and may trigger `OutOfMemoryError` unless you increase `-Xmx` or reduce the dimension.
