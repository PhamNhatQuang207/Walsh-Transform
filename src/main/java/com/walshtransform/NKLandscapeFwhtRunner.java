package com.walshtransform;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.BitSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Runs the Fast Walsh-Hadamard Transform on a random NK landscape and prints
 * the resulting Walsh coefficients.
 */
public final class NKLandscapeFwhtRunner {

    private NKLandscapeFwhtRunner() {
        // Utility class; prevent instantiation.
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n;
        int k;
        String outputPath;

        if (args.length >= 2) {
            n = Integer.parseInt(args[0]);
            k = Integer.parseInt(args[1]);
            outputPath = (args.length >= 3) ? args[2] : "nk_walsh_coefficients.csv";
        } else {
            System.out.print("Input dimension (n): ");
            n = scanner.nextInt();

            System.out.print("Input interactions (k): ");
            k = scanner.nextInt();

            System.out.print("Output CSV path (press Enter for default nk_walsh_coefficients.csv): ");
            scanner.nextLine();
            String outputInput = scanner.nextLine();
            outputPath = outputInput.isBlank() ? "nk_walsh_coefficients.csv" : outputInput;
        }

        NKLandscape landscape = new NKLandscape(n, k);
        Map<BitSet, Double> weights = BigNKLandscapeWalshTransformer.extractCoefficients(landscape);

        int totalStates = weights.size();

        System.out.println("\n--- FWHT on NK Landscape ---");
        System.out.println("Dimension: " + n + ", k: " + k + ", nonzero: " + totalStates);
        System.out.println();
        System.out.print(landscape.describeProperties());

        writeCsv(weights, n, outputPath);
        System.out.println("Saved coefficients to: " + outputPath);

        scanner.close();
    }

    private static String toBitmask(BitSet value, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = n - 1; i >= 0; i--) {
            sb.append(value.get(i) ? '1' : '0');
        }
        return sb.toString();
    }

    private static void writeCsv(Map<BitSet, Double> weights, int n, String outputPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputPath))) {
            writer.write("order,indices,coefficient");
            writer.newLine();
            weights.entrySet().stream()
                .sorted((left, right) -> {
                    int leftOrder = left.getKey().cardinality();
                    int rightOrder = right.getKey().cardinality();
                    if (leftOrder != rightOrder) {
                        return Integer.compare(leftOrder, rightOrder);
                    }
                    double leftAbs = Math.abs(left.getValue());
                    double rightAbs = Math.abs(right.getValue());
                    int byAbs = Double.compare(rightAbs, leftAbs);
                    if (byAbs != 0) {
                        return byAbs;
                    }
                    return toBitmask(left.getKey(), n).compareTo(toBitmask(right.getKey(), n));
                })
                .forEach(entry -> {
                    try {
                        writer.write(Integer.toString(entry.getKey().cardinality()));
                        writer.write(',');
                        writer.write(toIndices(entry.getKey()));
                        writer.write(',');
                        writer.write(Double.toString(entry.getValue()));
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV output", e);
        }
    }

    private static String toIndices(BitSet value) {
        StringBuilder sb = new StringBuilder();
        for (int bit = value.nextSetBit(0); bit >= 0; bit = value.nextSetBit(bit + 1)) {
            if (sb.length() > 0) {
                sb.append(';');
            }
            sb.append(bit);
        }
        return sb.toString();
    }
}
