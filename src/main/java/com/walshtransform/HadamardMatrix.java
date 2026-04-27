package com.walshtransform;

/**
 * Utility class for generating Sylvester-type Hadamard matrices of order 2^n.
 *
 * <p>The generated matrix contains only +1 and -1 values and is constructed recursively:
 * H(0) = [1], H(n) = [[H(n-1), H(n-1)], [H(n-1), -H(n-1)]].</p>
 */
public class HadamardMatrix {
    private HadamardMatrix() {
        // Utility class; prevent instantiation.
    }

    /**
     * Generates a Hadamard matrix of size (2^n) x (2^n).
     *
     * @param n recursion depth; must be non-negative
     * @return matrix with entries in {+1, -1}
     * @throws IllegalArgumentException if n is negative or too large for 32-bit size
     */
    public static int[][] generate(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be >= 0");
        }
        if (n >= Integer.SIZE - 1) {
            throw new IllegalArgumentException("n is too large to build a 2^n matrix dimension as int");
        }

        int size = 1 << n;
        int[][] h = new int[size][size];
        fill(h, n, 0, 0, 1);
        return h;
    }

    /**
     * Recursively fills a submatrix using the Sylvester Hadamard construction.
     *
     * <p>For depth {@code n}, this method writes a {@code 2^n x 2^n} block whose top-left
     * corner is {@code (r, c)}. The block is split into four quadrants:
     * top-left, top-right, and bottom-left keep the same sign {@code val}, while bottom-right
     * uses {@code -val}.</p>
     *
     * @param mat target matrix to fill
     * @param n remaining recursion depth
     * @param r row index of the top-left corner of the current block
     * @param c column index of the top-left corner of the current block
     * @param val sign value to write at base case ({@code 1} or {@code -1})
     */
    private static void fill(int[][] mat, int n, int r, int c, int val) {
        if (n == 0) {
            mat[r][c] = val;
            return;
        }

        // Quadrant size for H(n-1) inside H(n).
        int s = 1 << (n - 1);
        fill(mat, n - 1, r, c, val);
        fill(mat, n - 1, r, c + s, val);
        fill(mat, n - 1, r + s, c, val);
        fill(mat, n - 1, r + s, c + s, -val);
    }
}