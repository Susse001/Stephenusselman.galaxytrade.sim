package com.stephenu.gts.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.stephenu.gts.simulation.TradeOpportunity;
import com.stephenu.gts.simulation.dto.TradeOpportunityResponse;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.trader.dto.TraderResponse;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

/**
 * Provides services for retrieving trader data.
 */
@Service
@RequiredArgsConstructor
public class TraderService {

    private final TraderRepository traderRepository;

    /**
	 * Returns all traders.
	 *
	 * @return A list of all traders.
	 */
    public List<TraderResponse> getAllTraders() {
        return traderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

	/**
	 * Returns the trader with the specified ID.
	 *
	 * @param id The identifier of the requested trader.
	 * @return The requested trader.
	 * @throws EntityNotFoundException If no trader exists with the specified ID.
	 */
    public TraderResponse getTraderById(Long id) {
        Trader trader = traderRepository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                            "Trader not found: " + id
                    ));

        return mapToResponse(trader);
    }

    private TradeOpportunityResponse mapToTradeResponse(
        TradeOpportunity trade) {
        if (trade == null) {
                return null;
        }

        return new TradeOpportunityResponse(
                trade.getId(),
                trade.getCommodity(),
                trade.getBuySystem().getId(),
                trade.getBuySystem().getName(),
                trade.getSellSystem().getId(),
                trade.getSellSystem().getName(),
                trade.getExpectedProfitPerUnit()
        );
        }

        private TraderResponse mapToResponse(Trader trader) {
			StarSystem system = trader.getCurrentSystem();

        return new TraderResponse(
                trader.getId(),
                trader.getName(),
                system.getId(),
                system.getName(),
                trader.getCredits(),
                trader.getStrategyProfile(),
                trader.getStatus(),
                mapToTradeResponse(trader.getCurrentTrade()),
                trader.getTravelTicksRemaining(),
                trader.getTotalTravelTicks(),
                trader.getCargoCommodity(),
                trader.getCargoAmount(),
                trader.getCargoCapacity()
        );
        }
}
