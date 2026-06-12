package com.stephenu.gts.simulation;

import com.stephenu.gts.market.MarketRepository;
import com.stephenu.gts.trader.TraderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationService {

    private final TraderRepository traderRepository;
    private final MarketRepository marketRepository;

    private long currentTick = 0;

    public long runTick() {

        currentTick++;

        long traderCount = traderRepository.count();
        long marketCount = marketRepository.count();

        System.out.println(
                "Tick "
                + currentTick
                + " | Traders="
                + traderCount
                + " Markets="
                + marketCount
        );

        return currentTick;
    }

    public long getCurrentTick() {
        return currentTick;
    }
}
