package com.walshtransform;

import java.util.Arrays;
import java.util.Scanner;

public final class WalshTransformFwhtCheck {
    private static final double DEFAULT_EPSILON = 1e-12;

    private WalshTransformFwhtCheck() {
        // Utility class; prevent instantiation.
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n;
        int maxValue;
        double epsilon;

        if (args.length >= 2) {
            n = Integer.parseInt(args[0]);
            maxValue = Integer.parseInt(args[1]);
            epsilon = (args.length >= 3) ? Double.parseDouble(args[2]) : DEFAULT_EPSILON;
        } else {
            System.out.print("Input dimension (n): ");
            n = scanner.nextInt();

            System.out.print("Input maximum value (Q): ");
            maxValue = scanner.nextInt();
            scanner.nextLine();

            System.out.print("Input epsilon (press Enter for default " + DEFAULT_EPSILON + "): ");
            String epsilonInput = scanner.nextLine();
            epsilon = epsilonInput.isBlank() ? DEFAULT_EPSILON : Double.parseDouble(epsilonInput);
        }

        BlackBoxFunction f = new RandomBlackBox(n, maxValue);
        double[] matrixWeights = WalshTransformer.computeWeights(f);
        double[] fwhtWeights = FastWalshTransformer.calculateWalshCoefficients(f);

        int totalStates = matrixWeights.length;
        ComparisonResult result = compare(matrixWeights, fwhtWeights, epsilon);

        System.out.println("\n--- FWHT vs Matrix Walsh Transform ---");
        System.out.println("Dimension: " + n + ", states: " + totalStates);
        System.out.println("Epsilon: " + epsilon);

        System.out.printf("\n%-10s | %-20s | %-20s | %-10s%n",
            "Index k", "Matrix Method", "FWHT Method", "Status");
        System.out.println("----------------------------------------------------------------------------------");

        int displayLimit = Math.min(totalStates, 20);
        boolean isCorrect = true;
        for (int k = 0; k < totalStates; k++) {
            double diff = Math.abs(matrixWeights[k] - fwhtWeights[k]);
            boolean match = diff < epsilon;
            if (!match) {
                isCorrect = false;
            }

            if (k < displayLimit) {
                System.out.printf("%-10d | %-20.10f | %-20.10f | %-10s%n",
                    k, matrixWeights[k], fwhtWeights[k], match ? "MATCH" : "FAIL");
            }
        }

        if (totalStates > displayLimit) {
            System.out.println("... and " + (totalStates - displayLimit) + " other coefficients.");
        }

        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("RESULT: " + (isCorrect ? "SUCCESS - Both methods match!" : "FAILURE - There are discrepancies!"));
        System.out.println("Max difference: " + result.maxDiff + " at index " + result.maxDiffIndex);

        scanner.close();
    }

    private static ComparisonResult compare(double[] matrixWeights, double[] fwhtWeights, double epsilon) {
        int size = matrixWeights.length;
        if (fwhtWeights.length != size) {
            throw new IllegalArgumentException("weight arrays must have the same length");
        }

        boolean match = true;
        double maxDiff = 0.0;
        int maxDiffIndex = -1;
        int[] mismatches = new int[size];
        int mismatchCount = 0;

        for (int i = 0; i < size; i++) {
            double diff = Math.abs(matrixWeights[i] - fwhtWeights[i]);
            if (diff > maxDiff) {
                maxDiff = diff;
                maxDiffIndex = i;
            }
            if (diff > epsilon) {
                match = false;
                mismatches[mismatchCount++] = i;
            }
        }

        return new ComparisonResult(match, maxDiff, maxDiffIndex, Arrays.copyOf(mismatches, mismatchCount));
    }

    private static final class ComparisonResult {
        private final boolean match;
        private final double maxDiff;
        private final int maxDiffIndex;
        private final int[] mismatchIndices;

        private ComparisonResult(boolean match, double maxDiff, int maxDiffIndex, int[] mismatchIndices) {
            this.match = match;
            this.maxDiff = maxDiff;
            this.maxDiffIndex = maxDiffIndex;
            this.mismatchIndices = mismatchIndices;
        }
    }
}
