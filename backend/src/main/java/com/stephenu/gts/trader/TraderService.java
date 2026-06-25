package com.stephenu.gts.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.stephenu.gts.simulation.TradeOpportunity;
import com.stephenu.gts.simulation.dto.TradeOpportunityResponse;
import com.stephenu.gts.trader.dto.TraderResponse;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TraderService {

    private final TraderRepository traderRepository;

    public List<TraderResponse> getAllTraders() {
        return traderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TraderResponse getTraderById(Long id) {
        Trader trader = traderRepository.findById(id)
                .orElseThrow(() ->
                    new EntityNotFoundException(
                            "Trader not found: " + id
                    ));

        return toResponse(trader);
    }

    private TradeOpportunityResponse toTradeResponse(
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

        private TraderResponse toResponse(Trader trader) {

        return new TraderResponse(
                trader.getId(),
                trader.getName(),
                trader.getCurrentSystem().getId(),
                trader.getCurrentSystem().getName(),
                trader.getCredits(),
                trader.getStrategyProfile(),
                trader.getStatus(),
                toTradeResponse(trader.getCurrentTrade()),
                trader.getTravelTicksRemaining(),
                trader.getTotalTravelTicks(),
                trader.getCargoCommodity(),
                trader.getCargoAmount(),
                trader.getCargoCapacity()
        );
        }
}
