package com.walshtransform;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Input dimension n and maximum value Q for the random black-box function
        System.out.print("Input dimension (n): ");
        int n = scanner.nextInt();

        System.out.print("Input maximum value (Q): ");
        double Q = scanner.nextDouble();

        // 2. Initialize the random black-box function
        BlackBoxFunction f = new RandomBlackBox(n, Q);
        int totalStates = BinaryVectorUtils.checkedPowerOfTwo(n);
        System.out.println("\n---  Input/Output of Black-box ---");
        System.out.printf("%-15s | %-15s\n", "Input (x)", "Output f(x)");
        System.out.println("------------------------------------------");
        
        for (int i = 0; i < totalStates; i++) {
            // Chuyển chỉ số vòng lặp thành vector binary để làm input [cite: 66, 77]
            int[] binaryInput = BinaryVectorUtils.intToBinaryVector(i, n);
            double output = f.evaluate(binaryInput);
            
            System.out.printf("%-15s | %-15.4f\n", Arrays.toString(binaryInput), output);
        }
        System.out.println("------------------------------------------\n");
        System.out.println("\n--- Start calculating " + totalStates + " Walsh coefficients ---");
        
        
        // 3. First way: Using Matrix Method
        long startMatrix = System.currentTimeMillis();
        double[] weightsMatrix = WalshTransformer.computeWeights(f);
        long endMatrix = System.currentTimeMillis();

        // 4. Second way: Using Direct Summation
        long startSum = System.currentTimeMillis();
        double[] weightsSum = new double[totalStates];
        for (int k = 0; k < totalStates; k++) {
            weightsSum[k] = WalshTransformerDirect.computeSingleWeight(f, k);
        }
        long endSum = System.currentTimeMillis();

        // 5. Compare results and print output
        System.out.printf("\n%-10s | %-20s | %-20s | %-10s\n", "Index k", "Matrix Method", "Summation Method", "Status");
        System.out.println("----------------------------------------------------------------------------------");

        double EPSILON = 1e-12; 
        boolean isCorrect = true;
        
        int displayLimit = Math.min(totalStates, 20);
        for (int k = 0; k < totalStates; k++) {
            double diff = Math.abs(weightsMatrix[k] - weightsSum[k]);
            boolean match = diff < EPSILON;
            if (!match) isCorrect = false;

            if (k < displayLimit) {
                System.out.printf("%-10d | %-20.10f | %-20.10f | %-10s\n", 
                                  k, weightsMatrix[k], weightsSum[k], match ? "MATCH" : "FAIL");
            }
        }

        if (totalStates > displayLimit) {
            System.out.println("... và " + (totalStates - displayLimit) + " hệ số khác.");
        }

        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("RESULT: " + (isCorrect ? "SUCCESS - Both methods match!" : "FAILURE - There are discrepancies!"));
        System.out.println("Time for Matrix Method: " + (endMatrix - startMatrix) + " ms");
        System.out.println("Time for Summation Method:    " + (endSum - startSum) + " ms");

        scanner.close();
    }
}