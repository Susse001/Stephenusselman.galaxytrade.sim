package com.stephenu.gts.planet;

/**
 * Represents the quality of infrastructure available on a planet.
 */
public enum InfrastructureLevel {

    POOR("Poor"),
    MODEST("Modest"),
    GOOD("Good"),
    EXCELLENT("Excellent");

    private final String displayName;

    InfrastructureLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
