package org.example.calculations.enums;

public enum LoadType {
    AXIAL_FORCE("Axial Force (N)"),
    BENDING_MOMENT("Bending Moment (N.mm)"),
    TORSION("Torsion Torque (N.mm)"),
    INTERNAL_PRESSURE("Internal Pressure (MPa)"),;

    private final String displayName;
    LoadType(String displayName) { this.displayName = displayName; }
    @Override public String toString() { return displayName; }
}