package com.stephenu.gts.commodity;

/**
 * Defines the commodities that can be traded within the simulation.
 */
public enum CommodityType {

    //Tier 1
    FOOD("Food"),
    WATER("Water"),
    ORE("Ore"),
    RARE_METALS("Rare Metals"),
    ORGANICS("Organics"),

    //Tier 2
    CHEMICAL_COMPOUNDS("Chemical Compounds"),
    REFINED_METALS("Refined Metals"),
    ADVANCED_COMPONENTS("Advanced Components"),

    //Tier 3
    ELECTRONICS("Electronics"),
    MEDICINE("Medicine"),
    INDUSTRIAL_PARTS("Industrial Parts"),

    //Unique
    LUXURY_GOODS("Luxury Goods");

    private final String displayName;

    CommodityType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly name of the commodity.
     *
     * @return The display name of the commodity.
     */
    public String getDisplayName() {
        return displayName;
    }
}
