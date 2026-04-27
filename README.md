# Stage Java Library

Java library project built with Maven and Java 17.

## Features

- `HadamardMatrix`: generates Sylvester-type Hadamard matrices of order `2^n`
- `BlackBoxFunction`: abstract base class for black-box function evaluation over integer vectors
- JUnit 5 tests for matrix generation properties and black-box base behavior

## Project Structure

- `src/main/java/com/example/HadamardMatrix.java`
- `src/main/java/com/example/BlackBoxFunction.java`
- `src/test/java/com/example/HadamardMatrixTest.java`
- `src/test/java/com/example/BlackBoxFunctionTest.java`

## Requirements

- JDK 17+
- Maven 3.8+

## Build and Test

```bash
mvn -B clean test
mvn -B package
```

## Usage Example

```java
int[][] h = HadamardMatrix.generate(2);
// h =
// [ 1,  1,  1,  1]
// [ 1, -1,  1, -1]
// [ 1,  1, -1, -1]
// [ 1, -1, -1,  1]
```

## Notes

- `HadamardMatrix.generate(n)` uses recursion with base case `H(0) = [1]`.
- Input `n` must be non-negative; invalid values throw `IllegalArgumentException`.
- Matrix values are stored as `int` (`+1` and `-1`).
