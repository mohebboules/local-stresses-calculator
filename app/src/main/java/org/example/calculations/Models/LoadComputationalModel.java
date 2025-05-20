package org.example.calculations.Models;

import org.example.calculations.enums.LoadType;
import org.example.calculations.enums.Variation;

public class LoadComputationalModel {
    private final LoadType loadType;
    private final Variation variation;
    private final double staticValue;
    private final double a, b;

    public LoadComputationalModel(LoadType loadType, Variation variation, double staticValue, double a, double b) {
        this.loadType = loadType;
        this.variation = variation;
        this.staticValue = staticValue;
        this.a = a;
        this.b = b;
    }

    public LoadType getLoadType() { return loadType; }
    public Variation getVariation() { return variation; }
    public double getStaticValue() { return staticValue; }
    public double getA() { return a; }
    public double getB() { return b; }

    public double evaluate(double t, double w) {
        if (variation == Variation.STATIC) {
            return staticValue;
        } else {
            return a + b * Math.sin(w * t);
        }
    }
}