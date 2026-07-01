package com.stephenu.gts.trader.dto;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.simulation.dto.TradeOpportunityResponse;
import com.stephenu.gts.trader.StrategyProfile;
import com.stephenu.gts.trader.TraderStatus;

/**
 * Represents the API response for a trader.
 *
 * @param id The unique identifier of the trader.
 * @param name The trader's name.
 * @param currentSystemId The identifier of the trader's current star system.
 * @param currentSystemName The name of the trader's current star system.
 * @param credits The trader's available credits.
 * @param strategyProfile The trader's decision-making profile.
 * @param status The trader's current simulation state.
 * @param currentTrade The trader's assigned trade opportunity.
 * @param travelTicksRemaining The remaining travel time.
 * @param totalTravelTicks The total travel time for the current journey.
 * @param cargoCommodity The commodity currently being transported.
 * @param cargoAmount The quantity of cargo being transported.
 * @param cargoCapacity The trader's maximum cargo capacity.
 */
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
