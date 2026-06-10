package com.stephenu.gts.commodity.dto;

import com.stephenu.gts.commodity.CommodityType;

public record CommodityResponse(
        Long id,
        CommodityType type,
        double basePrice
) {}