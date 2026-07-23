package com.stephenu.gts.starsystem;

/**
 * Represents the major galactic regions used during world generation.
 */
public enum Region {
    CORE("Core"),
    INNER_RIM("Inner Rim"),
    OUTER_RIM("Outer Rim");

    private final String displayName;

    Region(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
