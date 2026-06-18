package com.stephenu.gts.market.dto;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.Region;

public record MarketResponse(
        Long id,
        Long systemId,
        String systemName,
        Region region,
        CommodityType commodityType,
        Integer price,
        Integer supply,
        Integer demand
) {}
