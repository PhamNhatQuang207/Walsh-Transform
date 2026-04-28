package com.walshtransform;

import java.util.Scanner;

public class WalshTransformBenchmark {
    private static final int DEFAULT_SAMPLES = 5;

    public static void main(String[] args) {
        BenchmarkConfig config = (args.length >= 3)
            ? BenchmarkConfig.fromArgs(args)
            : BenchmarkConfig.fromScanner(new Scanner(System.in));

        if (config == null) {
            return;
        }

        runBenchmark(config);
    }

    private static void runBenchmark(BenchmarkConfig config) {
        System.out.println("\n--- Walsh Transform Benchmark ---");
        System.out.printf("Dimensions: %d to %d%n", config.nStart, config.nEnd);
        System.out.printf("Random max value Q: %.4f%n", config.maxValue);
        System.out.printf("Samples per dimension: %d%n%n", config.samples);

        System.out.printf("%-10s | %-12s | %-18s%n",
            "Dimension", "States", "Avg Matrix (ms)");
        System.out.println("------------------------------------------------------------");

        for (int n = config.nStart; n <= config.nEnd; n++) {
            int size = BinaryVectorUtils.checkedPowerOfTwo(n);
            double totalMatrixNs = 0.0;

            for (int i = 0; i < config.samples; i++) {
                BlackBoxFunction f = new RandomBlackBox(n, config.maxValue);

                long startMatrix = System.nanoTime();
                WalshTransformer.computeWeights(f);
                long endMatrix = System.nanoTime();
                totalMatrixNs += (endMatrix - startMatrix);

            }

            double avgMatrixMs = totalMatrixNs / config.samples / 1_000_000.0;

            System.out.printf("%-10d | %-12d | %-18.4f%n",
                    n, size, avgMatrixMs);
        }

        System.out.println("------------------------------------------------------------");
        System.out.println("Done.");
    }

    private static final class BenchmarkConfig {
        private final int nStart;
        private final int nEnd;
        private final double maxValue;
        private final int samples;

        private BenchmarkConfig(int nStart, int nEnd, double maxValue, int samples) {
            this.nStart = nStart;
            this.nEnd = nEnd;
            this.maxValue = maxValue;
            this.samples = samples;
        }

        private static BenchmarkConfig fromArgs(String[] args) {
            try {
                int nStart = Integer.parseInt(args[0]);
                int nEnd = Integer.parseInt(args[1]);
                double maxValue = Double.parseDouble(args[2]);
                int samples = (args.length >= 4) ? Integer.parseInt(args[3]) : DEFAULT_SAMPLES;
                return validate(nStart, nEnd, maxValue, samples);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid arguments. Usage: nStart nEnd Q [samples]");
                return null;
            }
        }

        private static BenchmarkConfig fromScanner(Scanner scanner) {
            System.out.print("Start dimension (nStart): ");
            int nStart = scanner.nextInt();

            System.out.print("End dimension (nEnd): ");
            int nEnd = scanner.nextInt();

            System.out.print("Input maximum value (Q): ");
            double maxValue = scanner.nextDouble();

            return validate(nStart, nEnd, maxValue, DEFAULT_SAMPLES);
        }

        private static BenchmarkConfig validate(int nStart, int nEnd, double maxValue, int samples) {
            if (nStart < 1 || nEnd < nStart) {
                System.out.println("Invalid dimension range. Expect 1 <= nStart <= nEnd.");
                return null;
            }
            if (maxValue <= 0) {
                System.out.println("Q must be positive.");
                return null;
            }
            if (samples <= 0) {
                System.out.println("Samples must be positive.");
                return null;
            }
            return new BenchmarkConfig(nStart, nEnd, maxValue, samples);
        }
    }
}
