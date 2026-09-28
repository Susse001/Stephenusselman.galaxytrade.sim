package com.stephenu.gts.planet;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.EnumType;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.EnumMap;
import java.util.Map;

import com.stephenu.gts.commodity.CommodityType;

@Embeddable
@Getter
@NoArgsConstructor
public class PlanetConsumptionProfile {

    @ElementCollection
    @CollectionTable(
        name = "planet_consumption",
        joinColumns = @JoinColumn(name = "planet_id")
    )
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "commodity")
    @Column(name = "amount", nullable = false)
    private Map<CommodityType, Double> consumption =
            new EnumMap<>(CommodityType.class);

    public PlanetConsumptionProfile(
            Map<CommodityType, Double> consumption) {

        this.consumption = consumption;
    }
}