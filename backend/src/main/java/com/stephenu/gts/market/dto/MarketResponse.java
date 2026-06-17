package com.stephenu.gts.market.dto;

import com.stephenu.gts.commodity.CommodityType;

public record MarketResponse(
        Long id,
        Long systemId,
        String systemName,
        String region,
        CommodityType commodityType,
        Integer price,
        Integer supply,
        Integer demand
) {}
