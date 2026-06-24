package com.stephenu.gts.commodity;

public enum CommodityType {

    FOOD("Food"),
    WATER("Water"),
    ORE("Ore"),
    RARE_METALS("Rare Metals"),
    ORGANICS("Organics"),

    CHEMICAL_COMPOUNDS("Chemical Compounds"),
    REFINED_METALS("Refined Metals"),
    ADVANCED_COMPONENTS("Advanced Components"),

    ELECTRONICS("Electronics"),
    MEDICINE("Medicine"),
    INDUSTRIAL_PARTS("Industrial Parts"),

    LUXURY_GOODS("Luxury Goods");

    private final String displayName;

    CommodityType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
