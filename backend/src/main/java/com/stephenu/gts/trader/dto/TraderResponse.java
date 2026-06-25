package com.stephenu.gts.trader.dto;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.simulation.dto.TradeOpportunityResponse;
import com.stephenu.gts.trader.StrategyProfile;
import com.stephenu.gts.trader.TraderStatus;

public record TraderResponse(
        Long id,
        String name,
        Long currentSystemId,
        String currentSystemName,
        Long credits,
        StrategyProfile strategyProfile,
        TraderStatus status,
        TradeOpportunityResponse currentTrade,
        Integer travelTicksRemaining,
        Integer totalTravelTicks,
        CommodityType cargoCommodity,
        Integer cargoAmount,
        Integer cargoCapacity
) {}
