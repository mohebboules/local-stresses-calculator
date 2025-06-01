package org.example.calculations;

import java.util.ArrayList;
import java.util.List;

import org.example.calculations.Models.GlobalStressModel;
import org.example.calculations.Models.LoadComputationalModel;
import org.example.calculations.Models.StaticStressModel;
import org.example.calculations.Models.StressModel;
import org.example.calculations.Models.VariableStressModel;
import org.example.calculations.enums.LoadType;
import org.example.calculations.enums.TankType;
import org.example.calculations.enums.Variation;

public class GlobalStressCalculator {

  public GlobalStressModel calculateAllStresses(CalculationInputBundle bundle) {
    List<StressModel> sigmaX = new ArrayList<StressModel>();
    List<StressModel> sigmaY =  new ArrayList<StressModel>();
    List<StressModel> tauXY =  new ArrayList<StressModel>();
    sigmaX.add(new StaticStressModel(0));
    sigmaY.add(new StaticStressModel(0));
    tauXY.add(new StaticStressModel(0));
    switch (bundle.tankType) {
        case TankType.OPEN_CYLINDER:
        {    
          if (bundle.loads.containsKey(LoadType.AXIAL_FORCE)) {
            sigmaX.add(calculateAxialStress(bundle.loads.get(LoadType.AXIAL_FORCE), bundle.externalDiameter, bundle.internalDiameter));
          }
          if (bundle.loads.containsKey(LoadType.BENDING_MOMENT)) {
            sigmaX.add(calculateBendingStress(bundle.loads.get(LoadType.BENDING_MOMENT), bundle.externalDiameter, bundle.internalDiameter));
          }
          if (bundle.loads.containsKey(LoadType.TORSION)) {
            tauXY.add(calculateTorsionStress(bundle.loads.get(LoadType.TORSION), bundle.externalDiameter, bundle.internalDiameter));
          }
          if (bundle.loads.containsKey(LoadType.INTERNAL_PRESSURE)) {
            sigmaY.add(calculateCylinderTangentialStress(bundle.loads.get(LoadType.INTERNAL_PRESSURE), bundle.externalDiameter, bundle.internalDiameter));
          }
          break;
        }
        case TankType.CLOSED_CYLINDER:
        {    
          if (bundle.loads.containsKey(LoadType.AXIAL_FORCE)) {
            sigmaX.add(calculateAxialStress(bundle.loads.get(LoadType.AXIAL_FORCE), bundle.externalDiameter, bundle.internalDiameter));
          }
          if (bundle.loads.containsKey(LoadType.BENDING_MOMENT)) {
            sigmaX.add(calculateBendingStress(bundle.loads.get(LoadType.BENDING_MOMENT), bundle.externalDiameter, bundle.internalDiameter));
          }
          if (bundle.loads.containsKey(LoadType.TORSION)) {
            tauXY.add(calculateTorsionStress(bundle.loads.get(LoadType.TORSION), bundle.externalDiameter, bundle.internalDiameter));
          }
          if (bundle.loads.containsKey(LoadType.INTERNAL_PRESSURE)) {
            sigmaX.add(calculateCylinderLongitudinalStress(bundle.loads.get(LoadType.INTERNAL_PRESSURE), bundle.externalDiameter, bundle.internalDiameter));
            sigmaY.add(calculateCylinderTangentialStress(bundle.loads.get(LoadType.INTERNAL_PRESSURE), bundle.externalDiameter, bundle.internalDiameter));
          }
          break;
        }
        case TankType.SPHERICAL_TANK:
        {    
          if (bundle.loads.containsKey(LoadType.INTERNAL_PRESSURE)) {
            sigmaY.add(calculateSphericalTankStress(bundle.loads.get(LoadType.INTERNAL_PRESSURE), bundle.externalDiameter, bundle.internalDiameter));
            sigmaX.add(calculateSphericalTankStress(bundle.loads.get(LoadType.INTERNAL_PRESSURE), bundle.externalDiameter, bundle.internalDiameter));
          }
          break;
        }
        default:
          throw new IllegalArgumentException("Invalid tank type: " + bundle.tankType);

    }
       
    return new GlobalStressModel(sigmaX, sigmaY, tauXY);
  }

  private StressModel calculateTorsionStress(LoadComputationalModel torque, double externalDiameter, double internalDiameter) {
    double polarMomentOfInertia = (Math.PI / 32) * (Math.pow(externalDiameter, 4) - Math.pow(internalDiameter, 4));
    if (torque.getVariation() == Variation.STATIC) {
        double value = torque.getStaticValue() * externalDiameter / (2 * polarMomentOfInertia);
        return new StaticStressModel(value);
    } else {
        double torqueA = torque.getA();
        double torqueB = torque.getB();
        double stressA = torqueA * externalDiameter / (2 * polarMomentOfInertia); 
        double stressB = torqueB * externalDiameter / (2 * polarMomentOfInertia);
        return new VariableStressModel(
            stressA,
            stressB
        );
    }
  }
  
  private StressModel calculateBendingStress (LoadComputationalModel moment, double externalDiameter, double internalDiameter) {
    double momentOfInertia = (Math.PI / 64) * (Math.pow(externalDiameter, 4) - Math.pow(internalDiameter, 4));
      if (moment.getVariation() == Variation.STATIC) {
          double value = moment.getStaticValue() * externalDiameter / (2 * momentOfInertia);
          return new StaticStressModel(value);
      } else {
          double momentA = moment.getA();
          double momentB = moment.getB();
          double stressA = momentA * externalDiameter / ( 2 * momentOfInertia); 
          double stressB = momentB * externalDiameter / (2 *  momentOfInertia);
          return new VariableStressModel(
              stressA,
              stressB
          );
      }
  }

  private StressModel calculateAxialStress(LoadComputationalModel axialForce, double externalDiameter, double internalDiameter) {
      double area = Math.PI * (Math.pow(externalDiameter, 2) - Math.pow(internalDiameter, 2)) / 4;
      if (axialForce.getVariation() == Variation.STATIC) {
          double value = axialForce.getStaticValue() / area;
          return new StaticStressModel(value);
      } else {
          double forceA = axialForce.getA();
          double forceB = axialForce.getB();
          double stressA = forceA / area; 
          double stressB = forceB / area;
          return new VariableStressModel(
              stressA,
              stressB
          );
      }
  }

  private StressModel calculateCylinderTangentialStress(LoadComputationalModel internalPressure, double externalDiameter, double internalDiameter) {
      double thickness = (externalDiameter - internalDiameter) / 2;
      
      if (internalPressure.getVariation() == Variation.STATIC) {
          double value = internalPressure.getStaticValue() * internalDiameter / (2 * thickness);
          return new StaticStressModel(value);
      } else {
          double pressureA = internalPressure.getA();
          double pressureB = internalPressure.getB();
          double stressA = pressureA * internalDiameter / (2 * thickness); 
          double stressB = pressureB * internalDiameter / (2 * thickness);
          return new VariableStressModel(
              stressA,
              stressB
          );
      }
  }

  private StressModel calculateCylinderLongitudinalStress(LoadComputationalModel internalPressure, double externalDiameter, double internalDiameter) {
      double thickness = (externalDiameter - internalDiameter) / 2;
      if (internalPressure.getVariation() == Variation.STATIC) {
          double value = internalPressure.getStaticValue() * internalDiameter / (4 * thickness);
          return new StaticStressModel(value);
      } else {
          double pressureA = internalPressure.getA();
          double pressureB = internalPressure.getB();
          double stressA = pressureA * internalDiameter / (4 * thickness); 
          double stressB = pressureB * internalDiameter / (4 * thickness);
          return new VariableStressModel(
              stressA,
              stressB
          );
      }
  }

  private StressModel calculateSphericalTankStress(LoadComputationalModel internalPressure, double externalDiameter, double internalDiameter) {
      double thickness = (externalDiameter - internalDiameter) / 2;
      if (internalPressure.getVariation() == Variation.STATIC) {
          double value = internalPressure.getStaticValue() * internalDiameter / (4 * thickness);
          return new StaticStressModel(value);
      } else {
          double pressureA = internalPressure.getA();
          double pressureB = internalPressure.getB();
          double stressA = pressureA * internalDiameter / (4 * thickness); 
          double stressB = pressureB * internalDiameter / (4 * thickness);
          return new VariableStressModel(
              stressA,
              stressB
          );
      }
  }
}