package org.example.calculations;

import org.example.calculations.Models.StressModel;

public class CalculationsOutputBundle {
    private final StressModel sigma1;
    private final StressModel sigma2;
    private final StressModel sigma6;

    public CalculationsOutputBundle(StressModel sigma1, StressModel sigma2, StressModel sigma6) {
        this.sigma1 = sigma1;
        this.sigma2 = sigma2;
        this.sigma6 = sigma6;
    }

    public StressModel getSigma1() {
        return sigma1;
    }

    public StressModel getSigma2() {
        return sigma2;
    }

    public StressModel getSigma6() {
        return sigma6;
    }
}