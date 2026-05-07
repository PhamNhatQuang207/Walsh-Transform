package com.walshtransform;

import java.util.Map;
import java.util.Scanner;

/**
 * Runs the Fast Walsh-Hadamard Transform on a random NK landscape and prints
 * the resulting Walsh coefficients.
 */
public final class NKLandscapeFwhtRunner {
    private static final int DEFAULT_DISPLAY_LIMIT = 20;

    private NKLandscapeFwhtRunner() {
        // Utility class; prevent instantiation.
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int n;
        int k;
        int displayLimit;

        if (args.length >= 2) {
            n = Integer.parseInt(args[0]);
            k = Integer.parseInt(args[1]);
            displayLimit = (args.length >= 3) ? Integer.parseInt(args[2]) : DEFAULT_DISPLAY_LIMIT;
        } else {
            System.out.print("Input dimension (n): ");
            n = scanner.nextInt();

            System.out.print("Input interactions (k): ");
            k = scanner.nextInt();

            System.out.print("Display limit (press Enter for default " + DEFAULT_DISPLAY_LIMIT + "): ");
            scanner.nextLine();
            String displayInput = scanner.nextLine();
            displayLimit = displayInput.isBlank() ? DEFAULT_DISPLAY_LIMIT : Integer.parseInt(displayInput);
        }

        NKLandscape landscape = new NKLandscape(n, k);
        Map<Long, Double> weights = NKLandscapeWalshTransformer.extractCoefficients(landscape);

        int totalStates = weights.size();
        int limit = Math.min(displayLimit, totalStates);

        System.out.println("\n--- FWHT on NK Landscape ---");
        System.out.println("Dimension: " + n + ", k: " + k + ", nonzero: " + totalStates);
        System.out.println();
        System.out.print(landscape.describeProperties());

        System.out.printf("\n%-10s | %-10s | %-20s%n", "Index k", "Mask", "FWHT Weight");
        System.out.println("--------------------------------------------------------");

        weights.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .limit(limit)
            .forEach(entry -> System.out.printf("%-10d | %-10s | %-20.10f%n",
                entry.getKey(),
                toBitmask(entry.getKey().intValue(), n),
                entry.getValue()));

        if (totalStates > limit) {
            System.out.println("... and " + (totalStates - limit) + " other coefficients.");
        }

        scanner.close();
    }

    private static String toBitmask(int value, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = n - 1; i >= 0; i--) {
            sb.append(((value >> i) & 1) == 1 ? '1' : '0');
        }
        return sb.toString();
    }
}
