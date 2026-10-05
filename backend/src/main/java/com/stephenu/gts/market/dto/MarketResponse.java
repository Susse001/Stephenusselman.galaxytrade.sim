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
 * @param inventory The current inventory of the commodity.
 * @param targetInventory The desired inventory level for the commodity.
 */
public record MarketResponse(
        Long id,
        Long systemId,
        String systemName,
        Region region,
        CommodityType commodityType,
        Integer price,
        Integer inventory,
        Integer targetInventory
) {}
