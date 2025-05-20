package org.example.calculations;

import java.util.Map;
import org.example.calculations.Models.LoadComputationalModel;
import org.example.calculations.enums.LoadType;
import org.example.calculations.enums.TankType;

public class CalculationInputBundle {
    public final TankType tankType;
    public final double internalDiameter;
    public final double externalDiameter;
    public final Map<LoadType, LoadComputationalModel> loads;

    public CalculationInputBundle(TankType tankType, double internalDiameter, double externalDiameter, Map<LoadType, LoadComputationalModel> loads) {
        this.tankType = tankType;
        this.internalDiameter = internalDiameter;
        this.externalDiameter = externalDiameter;
        this.loads = loads;
    }
}