package com.stephenu.gts.planet;

public enum OrbitZone {

    INNER("Inner Orbit"),
    MIDDLE("Middle Orbit"),
    OUTER("Outer Orbit");

    private final String displayName;

    OrbitZone(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
