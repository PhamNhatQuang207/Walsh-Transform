package com.walshtransform;
/**
 * This class represents a black-box function that can be evaluated at any point in the search space.
 * It is an abstract class that must be extended by specific implementations of black-box functions.
 */

public abstract class BlackBoxFunction{
    protected int n; // dimension of the search space
    public BlackBoxFunction(int n){
        this.n = n;
    }
    /**
     * Evaluates the black-box function at a given point in the search space.
    * @param x a boolean array representing vector in the search space where the function is
    * evaluated. Example: if n=3, x could be [true, false, true].
     * @return the real value of the black-box function with vector input.
     */

    public abstract double evaluate(boolean[] x);

    public int getDimension() {
        return n;
    }
}