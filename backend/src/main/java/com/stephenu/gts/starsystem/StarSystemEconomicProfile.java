package com.stephenu.gts.starsystem;

import java.util.EnumMap;
import java.util.Map;

import com.stephenu.gts.commodity.CommodityType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class StarSystemEconomicProfile {

    /**
     * Combined extraction capacity of all planets in the system.
     */
    @ElementCollection
    @CollectionTable(
            name = "system_extraction_capacity",
            joinColumns = @JoinColumn(name = "system_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> extractionCapacity =
            new EnumMap<>(CommodityType.class);

    /**
     * Combined manufacturing potential of all planets in the system.
     */
    @ElementCollection
    @CollectionTable(
            name = "system_manufacturing_potential",
            joinColumns = @JoinColumn(name = "system_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> manufacturingPotential =
            new EnumMap<>(CommodityType.class);

    /**
     * Current manufacturing allocation of the system.
     */
    @ElementCollection
    @CollectionTable(
            name = "system_manufacturing",
            joinColumns = @JoinColumn(name = "system_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> manufacturing =
            new EnumMap<>(CommodityType.class);

    /**
     * Combined consumption of all planets in the system.
     */
    @ElementCollection
    @CollectionTable(
            name = "system_consumption",
            joinColumns = @JoinColumn(name = "system_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> consumption =
            new EnumMap<>(CommodityType.class);

    public StarSystemEconomicProfile(
            Map<CommodityType, Double> extractionCapacity,
            Map<CommodityType, Double> manufacturingPotential,
            Map<CommodityType, Double> manufacturing,
            Map<CommodityType, Double> consumption) {

        this.extractionCapacity = extractionCapacity;
        this.manufacturingPotential = manufacturingPotential;
        this.manufacturing = manufacturing;
        this.consumption = consumption;
    }
}