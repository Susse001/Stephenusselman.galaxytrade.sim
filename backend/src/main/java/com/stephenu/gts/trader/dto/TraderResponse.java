package com.stephenu.gts.trader.dto;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.trader.StrategyProfile;

public record TraderResponse(
        Long id,
        String name,
        Long currentSystemId,
        String currentSystemName,
        Integer credits,
        StrategyProfile strategyProfile,
        CommodityType targetCommodity,
        Long targetSystemId,
        String targetSystemName
) {}
