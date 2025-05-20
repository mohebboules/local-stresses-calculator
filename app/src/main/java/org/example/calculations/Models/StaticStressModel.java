package org.example.calculations.Models;

public class StaticStressModel implements StressModel {
    private final double value;

    public StaticStressModel(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(double t, double w) {
        return value;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public double getStaticValue() {
        return value;
    }

    @Override
    public double getA() {
        throw new UnsupportedOperationException("Static stress has no A parameter.");
    }

    @Override
    public double getB() {
        throw new UnsupportedOperationException("Static stress has no B parameter.");
    }
}