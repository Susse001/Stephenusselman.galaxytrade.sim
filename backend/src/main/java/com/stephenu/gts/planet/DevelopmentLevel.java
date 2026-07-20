package com.stephenu.gts.planet;

/**
 * Represents the overall technological and industrial development
 * of a planet.
 */
public enum DevelopmentLevel {

    COLONIAL("Colonial"),
    AGRARIAN("Agrarian"),
    DEVELOPING("Developing"),
    INDUSTRIAL("Industrial"),
    ADVANCED("Advanced");

    private final String displayName;

    DevelopmentLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
