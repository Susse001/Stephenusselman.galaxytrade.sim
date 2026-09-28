package com.stephenu.gts.planet;

import java.util.EnumMap;
import java.util.Map;

import com.stephenu.gts.commodity.CommodityType;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class PlanetProductionProfile {

    @ElementCollection
    @CollectionTable(
            name = "planet_extraction_capacity",
            joinColumns = @JoinColumn(name = "planet_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> extractionCapacity =
            new EnumMap<>(CommodityType.class);

    @ElementCollection
    @CollectionTable(
            name = "planet_manufacturing_potential",
            joinColumns = @JoinColumn(name = "planet_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> manufacturingPotential =
            new EnumMap<>(CommodityType.class);

    public PlanetProductionProfile(
            Map<CommodityType, Double> extractionCapacity,
            Map<CommodityType, Double> manufacturingPotential) {

        this.extractionCapacity = extractionCapacity;
        this.manufacturingPotential = manufacturingPotential;
    }
}
