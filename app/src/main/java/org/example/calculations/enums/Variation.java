package org.example.calculations.enums;

public enum Variation {
    STATIC("Static"),
    VARIABLE("Variable (A + B sin(wt))");

    private final String displayName;
    Variation(String displayName) { this.displayName = displayName; }
    public String getDisplayName() { return displayName; }
    @Override public String toString() { return displayName; }
}