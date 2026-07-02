package com.stephenu.gts.simulation.dto;

import com.stephenu.gts.commodity.CommodityType;

/**
 * Represents the API response for a trade opportunity.
 *
 * @param id The unique identifier of the trade opportunity.
 * @param commodity The commodity being traded.
 * @param buySystemId The identifier of the purchase system.
 * @param buySystemName The name of the purchase system.
 * @param sellSystemId The identifier of the destination system.
 * @param sellSystemName The name of the destination system.
 * @param expectedProfitPerUnit The estimated profit earned per unit.
 */
public record TradeOpportunityResponse(
        Long id,
        CommodityType commodity,
        Long buySystemId,
        String buySystemName,
        Long sellSystemId,
        String sellSystemName,
        Integer expectedProfitPerUnit
) {}
