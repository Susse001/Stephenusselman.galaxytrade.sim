package com.stephenu.gts.planet;

public enum ResourceLevel {

    NONE("None"),
    POOR("Poor"),
    AVERAGE("Average"),
    RICH("Rich"),
    ABUNDANT("Abundant");

    private final String displayName;

    ResourceLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
