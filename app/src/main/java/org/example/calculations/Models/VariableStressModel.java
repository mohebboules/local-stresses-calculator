package org.example.calculations.Models;

public class VariableStressModel implements StressModel {
    private final double a;
    private final double b;

    public VariableStressModel(double a, double b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public double evaluate(double t, double w) {
        return a + b * Math.sin(w * t);
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public double getStaticValue() {
        throw new UnsupportedOperationException("Variable stress has no static value.");
    }

    @Override
    public double getA() {
        return a;
    }

    @Override
    public double getB() {
        return b;
    }
}