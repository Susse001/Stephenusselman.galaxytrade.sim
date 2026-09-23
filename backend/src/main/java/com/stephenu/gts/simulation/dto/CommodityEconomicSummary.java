package com.stephenu.gts.simulation.dto;

import com.stephenu.gts.commodity.CommodityType;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommodityEconomicSummary {

    private final CommodityType commodity;
    private final double production;
    private final double consumption;
    private final double inventory;

    public double getNetProduction() {
        return production - consumption;
    }
}
