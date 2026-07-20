package com.stephenu.gts.commodity;

/**
 * Defines the commodities that can be traded within the simulation.
 */
public enum CommodityType {

    //Tier 1
    FOOD("Food"),
    WATER("Water"),
    BIOMATERIALS("Biomaterials"),
    COMMON_METALS("Common Metals"),
    RARE_METALS("Rare Metals"),
    INDUSTRIAL_MINERALS("Industrial Minerals"),
    HYDROCARBONS("Hydrocarbons"),
    INDUSTRIAL_CHEMICALS("Industrial Chemicals"),
    RARE_ELEMENTS("Rare Elements"),

    //Tier 2
    REFINED_METALS("Refined Metals"),
    PETROCHEMICALS("Petrochemicals"),
    ADVANCED_MATERIALS("Advanced Materials"),
    MANUFACTURED_PARTS("Manufactured Parts"),
    ELECTRONIC_COMPONENTS("Electronic Components"),
    PHARMACEUTICALS("Pharmaceuticals"),
    FUEL("Fuel"),

    //Tier 3
    ELECTRONICS("Electronics"),
    MEDICAL_SUPPLIES("Medical Supplies"),
    INDUSTRIAL_MACHINERY("Industrial Machinery"),
    CAPITAL_GOODS("Capital Goods"),
    CONSUMER_GOODS("Consumer Goods"),

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
