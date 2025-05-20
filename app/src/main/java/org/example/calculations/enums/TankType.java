package org.example.calculations.enums;

public enum TankType {
    CLOSED_CYLINDER("Closed Cylinder"),
    OPEN_CYLINDER("Open Cylinder"),
    SPHERICAL_TANK("Spherical Tank");

    private final String displayName;

    TankType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}