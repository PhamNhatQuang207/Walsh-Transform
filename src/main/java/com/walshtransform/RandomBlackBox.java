package com.walshtransform;
import java.util.Random;


public class RandomBlackBox extends BlackBoxFunction {
    private final double[] values; 

    public RandomBlackBox(int n, double Q) {
        super(n);
        int size = BinaryVectorUtils.checkedPowerOfTwo(n);
        this.values = new double[size];
        
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            this.values[i] = random.nextDouble() * Q;
        }
    }

    @Override
    public double evaluate(int[] x) {
        int index = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 1) {
                index |= (1 << i);
            }
        }
        return values[index];
    }
}