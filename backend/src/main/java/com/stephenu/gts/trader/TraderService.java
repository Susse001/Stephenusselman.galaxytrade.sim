package com.stephenu.gts.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.stephenu.gts.trader.dto.TraderResponse;

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
                .orElseThrow();

        return toResponse(trader);
    }

    private TraderResponse toResponse(Trader trader) {

        return new TraderResponse(
                trader.getId(),
                trader.getName(),
                trader.getCurrentSystem().getId(),
                trader.getCurrentSystem().getName(),
                trader.getCredits(),
                trader.getStrategyProfile(),
                trader.getTargetCommodity(),

                trader.getTargetSystem() != null
                    ? trader.getTargetSystem().getId()
                    : null,

                trader.getTargetSystem() != null
                        ? trader.getTargetSystem().getName()
                        : null
        );
    }
}
