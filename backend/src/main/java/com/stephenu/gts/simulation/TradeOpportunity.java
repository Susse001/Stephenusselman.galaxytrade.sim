package com.stephenu.gts.simulation;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.StarSystem;

public record TradeOpportunity(
        CommodityType commodity,
        StarSystem buySystem,
        StarSystem sellSystem,
        int expectedProfit
) {}
