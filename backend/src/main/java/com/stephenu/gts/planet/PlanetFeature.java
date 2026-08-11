package com.stephenu.gts.planet;

/**
 * Represents notable planetary or orbital characteristics that
 * influence resource generation.
 *
 * Features supplement the planet's base type and help make
 * otherwise similar planets economically distinct.
 */
public enum PlanetFeature {

    ASTEROID_BELT("Asteroid Belt"),
    GAS_GIANT_ORBIT("Gas Giant Orbit"),
    HABITABLE_MOON("Habitable Moon"),
    METALLIC_MOON("Metallic Moons"),
    ICY_MOON("Icy Moon"),
    VOLCANIC_MOON("Volcanic Moon"),
    CRYOVOLCANIC_MOON("Cryovolcanic Moon"),
    RING_SYSTEM("Ring System"),
    TECTONIC_ACTIVITY("Tectonic Activity"),
    SALT_FLATS("Salt Flats"),
    DENSE_FORESTS("Dense Forests"),
    STRONG_GRAVITY("Strong Gravity"),
    ANCIENT_IMPACT_BASIN("Ancient Impact Basin"),
    SUBSURFACE_OCEAN("Subsurface Ocean"),
    SHALLOW_SEAS("Shallow Seas"),
    DENSE_ATMOSPHERE("Dense Atmosphere"),
    GEOTHERMAL_ACTIVITY("Geothermal Activity"),
    VOLCANIC_ACTIVITY("Volcanic Activity"),
    POWERFUL_WEATHER_SYSTEMS("Powerful Weather Systems"),
    WEAK_ATMOSPHERE("Weak Atmosphere"),
    RICH_FOSSIL_DEPOSITS("Rich Fossil Deposits");

    private final String displayName;

    PlanetFeature(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
