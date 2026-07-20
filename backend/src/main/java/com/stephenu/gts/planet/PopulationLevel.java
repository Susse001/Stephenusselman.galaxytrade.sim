package com.stephenu.gts.planet;

/**
 * Broad population categories used throughout the simulation.
 */
public enum PopulationLevel {

    TENS_OF_MILLIONS("10s of Millions"),
    HUNDREDS_OF_MILLIONS("100s of Millions"),
    BILLIONS("Billions"),
    TENS_OF_BILLIONS("10s of Billions");

    private final String displayName;

    PopulationLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
