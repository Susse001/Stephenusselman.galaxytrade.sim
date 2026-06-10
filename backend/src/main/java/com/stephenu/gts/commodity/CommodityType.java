package com.stephenu.gts.commodity;

public enum CommodityType {
    FOOD("Food"),
    FUEL("Fuel"),
    ORE("Ore"),
    MEDICINE("Medicine"),
    TECHNOLOGY("Technology"),
    LUXURY_GOODS("Luxury Goods");

    private final String displayName;

    CommodityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
