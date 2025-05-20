package org.example.calculations.Models;

import org.example.calculations.enums.LoadType;
import org.example.calculations.enums.Variation;

public class LoadInputModel {
    public LoadType loadType;
    public Variation variation = Variation.STATIC;
    public String staticValue = "";
    public String a = "", b = "";

    public LoadInputModel(LoadType loadType) {
        this.loadType = loadType;
    }

    public LoadComputationalModel toComputationalModel() throws NumberFormatException {
        if (variation == Variation.STATIC) {
            double value = Double.parseDouble(staticValue);
            return new LoadComputationalModel(loadType, variation, value, 0, 0);
        } else {
            double aVal = Double.parseDouble(a);
            double bVal = Double.parseDouble(b);
            return new LoadComputationalModel(loadType, variation, 0, aVal, bVal);
        }
    }
}