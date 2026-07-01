package com.stephenu.gts.commodity.dto;

import com.stephenu.gts.commodity.CommodityType;

/**
 * Represents the API response for a commodity.
 *
 * @param id The unique identifier of the commodity.
 * @param type The commodity type.
 * @param basePrice The commodity's base market price.
 */
public record CommodityResponse(
        Long id,
        CommodityType type,
        double basePrice
) {}