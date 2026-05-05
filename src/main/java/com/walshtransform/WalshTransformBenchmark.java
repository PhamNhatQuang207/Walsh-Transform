package com.walshtransform;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class WalshTransformBenchmark {
    private static final int DEFAULT_SAMPLES = 5;
    private static final String DEFAULT_CSV_PATH = "walsh_benchmark.csv";
    private static final String DEFAULT_CHART_PATH = "walsh_benchmark.png";

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
        System.out.printf("Random max value Q: %d%n", config.maxValue);
        System.out.printf("Samples per dimension: %d%n%n", config.samples);

        System.out.printf("%-10s | %-12s | %-18s | %-18s%n",
            "Dimension", "States", "Avg Matrix (ms)", "Avg FWHT (ms)");
        System.out.println("----------------------------------------------------------------------------------");

        List<BenchmarkResult> results = new ArrayList<>();

        for (int n = config.nStart; n <= config.nEnd; n++) {
            int size = BinaryVectorUtils.checkedPowerOfTwo(n);
            double totalMatrixNs = 0.0;
            double totalFwhtNs = 0.0;

            for (int i = 0; i < config.samples; i++) {
                BlackBoxFunction f = new RandomBlackBox(n, config.maxValue);

                long startMatrix = System.nanoTime();
                WalshTransformer.computeWeights(f);
                long endMatrix = System.nanoTime();
                totalMatrixNs += (endMatrix - startMatrix);

                long startFwht = System.nanoTime();
                FastWalshTransformer.calculateWalshCoefficients(f);
                long endFwht = System.nanoTime();
                totalFwhtNs += (endFwht - startFwht);

            }

            double avgMatrixMs = totalMatrixNs / config.samples / 1_000_000.0;
            double avgFwhtMs = totalFwhtNs / config.samples / 1_000_000.0;

            results.add(new BenchmarkResult(n, size, avgMatrixMs, avgFwhtMs));

            System.out.printf("%-10d | %-12d | %-18.4f | %-18.4f%n",
                    n, size, avgMatrixMs, avgFwhtMs);
        }

        System.out.println("----------------------------------------------------------------------------------");
        writeCsv(results, config.csvPath);
        writeChartImage(results, config.chartPath);
        System.out.println("Done.");
    }

    private static void writeCsv(List<BenchmarkResult> results, String csvPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath))) {
            writer.write("dimension,states,avg_matrix_ms,avg_fwht_ms");
            writer.newLine();
            for (BenchmarkResult result : results) {
                writer.write(result.dimension + "," + result.states + "," + String.format("%.6f", result.avgMatrixMs)
                    + "," + String.format("%.6f", result.avgFwhtMs));
                writer.newLine();
            }
            System.out.println("CSV written to: " + csvPath);
        } catch (IOException ex) {
            System.out.println("Failed to write CSV: " + ex.getMessage());
        }
    }

    private static void writeChartImage(List<BenchmarkResult> results, String imagePath) {
        if (results.isEmpty()) {
            return;
        }

        double maxValue = 0.0;
        int minDimension = results.get(0).dimension;
        int maxDimension = results.get(0).dimension;
        for (BenchmarkResult result : results) {
            maxValue = Math.max(maxValue, Math.max(result.avgMatrixMs, result.avgFwhtMs));
            if (result.dimension < minDimension) {
                minDimension = result.dimension;
            }
            if (result.dimension > maxDimension) {
                maxDimension = result.dimension;
            }
        }

        if (maxValue <= 0.0) {
            maxValue = 1.0;
        }

        int width = 900;
        int height = 600;
        int left = 70;
        int right = 20;
        int top = 30;
        int bottom = 70;
        int plotWidth = width - left - right;
        int plotHeight = height - top - bottom;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(left, top, left, top + plotHeight);
        g.drawLine(left, top + plotHeight, left + plotWidth, top + plotHeight);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Walsh Transform Avg Time", left, top - 10);

        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Dimension n", left + (plotWidth / 2) - 30, top + plotHeight + 40);
        g.drawString("Avg Time (ms)", 10, top + 10);

        int yTicks = 5;
        for (int i = 0; i <= yTicks; i++) {
            double ratio = i / (double) yTicks;
            int y = top + plotHeight - (int) Math.round(ratio * plotHeight);
            g.drawLine(left - 5, y, left, y);
            double value = maxValue * ratio;
            g.drawString(String.format("%.2f", value), 15, y + 4);
        }

        g.setStroke(new BasicStroke(2.0f));
        Color matrixColor = new Color(30, 90, 180);
        Color fwhtColor = new Color(200, 90, 20);

        drawSeries(g, results, minDimension, maxDimension, maxValue, left, top, plotWidth, plotHeight,
            matrixColor, true);
        drawSeries(g, results, minDimension, maxDimension, maxValue, left, top, plotWidth, plotHeight,
            fwhtColor, false);

        g.setColor(matrixColor);
        g.fillRect(left + plotWidth - 140, top + 5, 12, 12);
        g.setColor(Color.BLACK);
        g.drawString("Matrix", left + plotWidth - 120, top + 15);
        g.setColor(fwhtColor);
        g.fillRect(left + plotWidth - 70, top + 5, 12, 12);
        g.setColor(Color.BLACK);
        g.drawString("FWHT", left + plotWidth - 50, top + 15);

        g.dispose();

        try {
            ImageIO.write(image, "png", new File(imagePath));
            System.out.println("Chart written to: " + imagePath);
        } catch (IOException ex) {
            System.out.println("Failed to write chart: " + ex.getMessage());
        }
    }

    private static final class BenchmarkResult {
        private final int dimension;
        private final int states;
        private final double avgMatrixMs;
        private final double avgFwhtMs;

        private BenchmarkResult(int dimension, int states, double avgMatrixMs, double avgFwhtMs) {
            this.dimension = dimension;
            this.states = states;
            this.avgMatrixMs = avgMatrixMs;
            this.avgFwhtMs = avgFwhtMs;
        }
    }

    private static void drawSeries(
        Graphics2D g,
        List<BenchmarkResult> results,
        int minDimension,
        int maxDimension,
        double maxValue,
        int left,
        int top,
        int plotWidth,
        int plotHeight,
        Color color,
        boolean matrixSeries) {

        g.setColor(color);
        int prevX = -1;
        int prevY = -1;

        for (BenchmarkResult result : results) {
            int x;
            if (minDimension == maxDimension) {
                x = left + plotWidth / 2;
            } else {
                x = left + (int) Math.round(((result.dimension - minDimension)
                    / (double) (maxDimension - minDimension)) * plotWidth);
            }
            double value = matrixSeries ? result.avgMatrixMs : result.avgFwhtMs;
            int y = top + plotHeight - (int) Math.round((value / maxValue) * plotHeight);

            if (prevX >= 0) {
                g.drawLine(prevX, prevY, x, y);
            }

            g.fillOval(x - 3, y - 3, 6, 6);
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.valueOf(result.dimension), x - 6, top + plotHeight + 20);
            g.setColor(color);

            prevX = x;
            prevY = y;
        }
    }

    private static final class BenchmarkConfig {
        private final int nStart;
        private final int nEnd;
        private final int maxValue;
        private final int samples;
        private final String csvPath;
        private final String chartPath;

        private BenchmarkConfig(int nStart, int nEnd, int maxValue, int samples, String csvPath, String chartPath) {
            this.nStart = nStart;
            this.nEnd = nEnd;
            this.maxValue = maxValue;
            this.samples = samples;
            this.csvPath = csvPath;
            this.chartPath = chartPath;
        }

        private static BenchmarkConfig fromArgs(String[] args) {
            try {
                int nStart = Integer.parseInt(args[0]);
                int nEnd = Integer.parseInt(args[1]);
                int maxValue = Integer.parseInt(args[2]);
                int samples = (args.length >= 4) ? Integer.parseInt(args[3]) : DEFAULT_SAMPLES;
                String csvPath = (args.length >= 5) ? args[4] : DEFAULT_CSV_PATH;
                String chartPath = (args.length >= 6) ? args[5] : DEFAULT_CHART_PATH;
                return validate(nStart, nEnd, maxValue, samples, csvPath, chartPath);
            } catch (NumberFormatException ex) {
                System.out.println("Invalid arguments. Usage: nStart nEnd Q [samples] [csvPath] [chartPath]");
                return null;
            }
        }

        private static BenchmarkConfig fromScanner(Scanner scanner) {
            System.out.print("Start dimension (nStart): ");
            int nStart = scanner.nextInt();

            System.out.print("End dimension (nEnd): ");
            int nEnd = scanner.nextInt();

            System.out.print("Input maximum value (Q): ");
            int maxValue = scanner.nextInt();

            return validate(nStart, nEnd, maxValue, DEFAULT_SAMPLES, DEFAULT_CSV_PATH, DEFAULT_CHART_PATH);
        }

        private static BenchmarkConfig validate(int nStart, int nEnd, int maxValue, int samples, String csvPath, String chartPath) {
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
            String resolvedCsv = (csvPath == null || csvPath.isBlank()) ? DEFAULT_CSV_PATH : csvPath;
            String resolvedChart = (chartPath == null || chartPath.isBlank()) ? DEFAULT_CHART_PATH : chartPath;
            return new BenchmarkConfig(nStart, nEnd, maxValue, samples, resolvedCsv, resolvedChart);
        }
    }
}
