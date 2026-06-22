package com.stephenu.gts.simulation.dto;

import com.stephenu.gts.commodity.CommodityType;

public record TradeOpportunityResponse(
        Long id,
        CommodityType commodity,
        Long buySystemId,
        String buySystemName,
        Long sellSystemId,
        String sellSystemName,
        Integer expectedProfit
) {}
