package com.stephenu.gts.starsystem;

import java.util.EnumMap;
import java.util.Map;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.planet.Planet;
import com.stephenu.gts.planet.PlanetProductionProfile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StarSystemEconomicProfile {

    /**
     * Combined extraction capacity of all planets in the system.
     */
    private Map<CommodityType, Double> extractionCapacity =
            new EnumMap<>(CommodityType.class);

    /**
     * Combined manufacturing potential of all planets in the system.
     */
    private Map<CommodityType, Double> manufacturingPotential =
            new EnumMap<>(CommodityType.class);

    /**
    * Current manufacturing allocation of all planets in the system.
	*/
    private Map<CommodityType, Double> manufacturing =
            new EnumMap<>(CommodityType.class);

    /**
     * Combined natural and population-driven consumption
     * of all planets in the system.
     */
    private Map<CommodityType, Double> consumption =
            new EnumMap<>(CommodityType.class);

    public StarSystemEconomicProfile(
            Map<CommodityType, Double> extractionCapacity,
            Map<CommodityType, Double> manufacturingPotential,
            Map<CommodityType, Double> consumption) {

        this.extractionCapacity = extractionCapacity;
        this.manufacturingPotential = manufacturingPotential;
        this.consumption = consumption;
    }

    public StarSystemEconomicProfile generateProfile(
            StarSystem system) {

        Map<CommodityType, Double> extraction =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, Double> manufacturingPotential =
                new EnumMap<>(CommodityType.class);

        Map<CommodityType, Double> consumption =
                new EnumMap<>(CommodityType.class);

        for (Planet planet : system.getPlanets()) {

            PlanetProductionProfile production =
                    planet.getProductionProfile();

            addToMap(
                    extraction,
                    production.getExtractionCapacity()
            );

            addToMap(
                    manufacturingPotential,
                    production.getManufacturingPotential()
            );

            addToMap(
                    consumption,
                    planet.getConsumptionProfile().getConsumption()
            );
        }

        return new StarSystemEconomicProfile(
                extraction,
                manufacturingPotential,
                consumption
        );
    }

    private void addToMap(
        Map<CommodityType, Double> target,
        Map<CommodityType, Double> source) {

        source.forEach((commodity, value) ->
                target.merge(
                        commodity,
                        value,
                        Double::sum
                )
        );
    }
}