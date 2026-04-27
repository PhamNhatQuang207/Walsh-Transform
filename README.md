# Walsh Transform Java Library

Java library project built with Maven and Java 17 for calculating Walsh transforms.

## Features

- **HadamardMatrix**: generates Sylvester-type Hadamard matrices of order `2^n`.
- **WalshTransformer**: computes Walsh coefficients using the Hadamard matrix method.
- **WalshTransformerDirect**: computes Walsh coefficients using the direct summation method.
- **BlackBoxFunction**: abstract base class and random implementation for black-box function evaluation over integer vectors.
- **Main**: Interactive CLI application to test and compare both Walsh transform methods.
- JUnit 5 tests to verify the correctness of the matrix generation and transformations.

## Project Structure

- `src/main/java/com/walshtransform/HadamardMatrix.java`
- `src/main/java/com/walshtransform/BlackBoxFunction.java`
- `src/main/java/com/walshtransform/RandomBlackBox.java`
- `src/main/java/com/walshtransform/WalshTransformer.java`
- `src/main/java/com/walshtransform/WalshTransformerDirect.java`
- `src/main/java/com/walshtransform/BinaryVectorUtils.java`
- `src/main/java/com/walshtransform/Main.java`

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

## Notes

- `HadamardMatrix.generate(n)` uses recursion with base case `H(0) = [1]`.
- Input `n` must be non-negative; invalid values throw `IllegalArgumentException`.
- Matrix values are stored as `int` (`+1` and `-1`).
