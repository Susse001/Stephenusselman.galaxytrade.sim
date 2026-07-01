package com.stephenu.gts.market.dto;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.Region;

/**
 * Represents the API response for a market.
 *
 * @param id The unique identifier of the market.
 * @param systemId The identifier of the owning star system.
 * @param systemName The name of the owning star system.
 * @param region The region containing the star system.
 * @param commodityType The traded commodity.
 * @param price The current market price.
 * @param supply The available supply.
 * @param demand The current demand.
 */
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
