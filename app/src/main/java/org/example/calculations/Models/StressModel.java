 package org.example.calculations.Models;

public interface StressModel {
    double evaluate(double t, double w);
    boolean isStatic();
    double getStaticValue();
    double getA();
    double getB();

}