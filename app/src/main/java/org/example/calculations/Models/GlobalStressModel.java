package org.example.calculations.Models;

import java.util.List;

public class GlobalStressModel {
    private final List<StressModel> sigmaX;
    private final List<StressModel> sigmaY;
    private final List<StressModel> tauXY;

    public GlobalStressModel(List<StressModel> sigmaX, List<StressModel> sigmaY, List<StressModel> tauXY) {
        this.sigmaX = sigmaX;
        this.sigmaY = sigmaY;
        this.tauXY = tauXY;
    }

    public List<StressModel> getSigmaX() {
        return sigmaX;
    }

    public List<StressModel> getSigmaY() {
        return sigmaY;
    }

    public List<StressModel> getTauXY() {
        return tauXY;
    }
}