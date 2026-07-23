package com.stephenu.gts.planet;

/**
 * Represents the dominant environmental characteristics of a planet.
 *
 * Planet types influence available resources and production potential,
 * but do not directly determine industries.
 */
public enum PlanetType {

    CONTINENTAL("Continental"),
    OCEANIC("Oceanic"),
    ARID("Arid"),
    ALPINE("Alpine"),
    FROZEN("Frozen"),
    BARREN("Barren"),
    VOLCANIC("Volcanic"),
    CRYOVOLCANIC("Cryovolcanic");

    private final String displayName;

    PlanetType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
