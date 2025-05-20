package org.example.calculations;

import java.util.List;

import org.example.calculations.Models.StaticStressModel;
import org.example.calculations.Models.StressModel;
import org.example.calculations.Models.VariableStressModel;

public class LocalStressCalculator {

    public static CalculationsOutputBundle calculateLocalStress( double thetaDegrees, List<StressModel> sigmaX, List<StressModel> sigmaY, List<StressModel> tauXY) {
        double thetaRad = Math.toRadians(thetaDegrees);
        double c = Math.cos(thetaRad);
        double s = Math.sin(thetaRad);
        double c2 = c * c;
        double s2 = s * s;
        double sinCos = s * c;
        StressModel sumSigmaX =  globalStressSum(sigmaX);
        StressModel sumSigmay = globalStressSum(sigmaY);
        StressModel sumTauXy = globalStressSum(tauXY);
        if (sumSigmaX.isStatic() && sumSigmay.isStatic() && sumTauXy.isStatic()) {
            double localSigma1 = c2 * sumSigmaX.getStaticValue() + s2 * sumSigmay.getStaticValue() + 2 * sinCos * sumTauXy.getStaticValue();
            double localSigma2 = s2 * sumSigmaX.getStaticValue() + c2 * sumSigmay.getStaticValue() - 2 * sinCos * sumTauXy.getStaticValue();
            double localSigma6 = -sinCos * sumSigmaX.getStaticValue() + sinCos * sumSigmay.getStaticValue() + (c2 - s2) * sumTauXy.getStaticValue();
            return new CalculationsOutputBundle(new StaticStressModel(localSigma1), new StaticStressModel(localSigma2), new StaticStressModel(localSigma6));
        } else {
            VariableStressModel variablSumSigmaX = toVariableStressModel( globalStressSum(sigmaX));
            VariableStressModel variableSumSigmaY =  toVariableStressModel( globalStressSum(sigmaY));
            VariableStressModel variableSumTauXy =  toVariableStressModel( globalStressSum(tauXY));
            VariableStressModel localSigma1 = 
            new VariableStressModel(
            variablSumSigmaX.getA() * c2 + variableSumSigmaY.getA() * s2 + 2 * variableSumTauXy.getA() * sinCos,
             variablSumSigmaX.getB() * c2 + variableSumSigmaY.getB() *s2 + 2 * variableSumTauXy.getB() * sinCos
            );
            VariableStressModel localSigma2 = 
            new VariableStressModel(
           variablSumSigmaX.getA() * s2 + variableSumSigmaY.getA() * c2 - 2 * variableSumTauXy.getA() * sinCos,
             variablSumSigmaX.getB() * s2 + variableSumSigmaY.getB() * c2 - 2 * variableSumTauXy.getB() * sinCos
            );
            VariableStressModel localSigma6 = 
            new VariableStressModel(
            -variablSumSigmaX.getA() * sinCos + variableSumSigmaY.getA() * sinCos + (c2 - s2) * variableSumTauXy.getA(),
             -variablSumSigmaX.getB() * sinCos + variableSumSigmaY.getB() * sinCos + (c2 - s2) * variableSumTauXy.getB()
            );
            return new CalculationsOutputBundle(localSigma1, localSigma2, localSigma6);
        }
        

    }
    private static StressModel globalStressSum(List<StressModel> stresses) {
        boolean isStatic = false;
      
        // To check if all stresses are static
        for (StressModel stressModel : stresses) {
           if (stressModel.isStatic()) {
                isStatic = true;
            } else {
                isStatic = false;
            }
        }
    
        if (isStatic) {
            double sum = 0;
            for (StressModel stressModel : stresses) {
                sum += stressModel.getStaticValue();
            }
            return new StaticStressModel(sum);
        } else {
            double a = 0;
            double b = 0;
            for (StressModel stressModel : stresses) {
                if (stressModel instanceof VariableStressModel) {
                    VariableStressModel variableStress = (VariableStressModel) stressModel;
                    a += variableStress.getA();
                    b += variableStress.getB();
                }
                if (stressModel instanceof StaticStressModel) {
                    StaticStressModel staticStress = (StaticStressModel) stressModel;
                    a += staticStress.getStaticValue();
                }
            }
            return new VariableStressModel(a, b);    
        } 
    }
       private static VariableStressModel toVariableStressModel(StressModel stressModel) {
            if (stressModel instanceof VariableStressModel) {
                return (VariableStressModel) stressModel;
            } else if (stressModel instanceof StaticStressModel) {
                double staticValue = ((StaticStressModel) stressModel).getStaticValue();
                return new VariableStressModel(staticValue, 0);
            } else {
                throw new IllegalArgumentException("Unknown StressModel type");
            }
        }
}
