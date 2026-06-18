package com.stephenu.gts.trader;

import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
@RequiredArgsConstructor
public class TraderDataLoader
        implements CommandLineRunner {

    private final TraderRepository traderRepository;
    private final StarSystemRepository starSystemRepository;

    @Override
    public void run(String... args) {

        if (traderRepository.count() > 0) {
            return;
        }

        List<StarSystem> systems =
                starSystemRepository.findAll();

        int traderNumber = 1;

        for (StarSystem system : systems) {

            traderRepository.save(
                    new Trader(
                            "Trader " + traderNumber++,
                            system,
                            10000,
                            StrategyProfile.CONSERVATIVE
                    )
            );

            traderRepository.save(
                    new Trader(
                            "Trader " + traderNumber++,
                            system,
                            10000,
                            StrategyProfile.AGGRESSIVE
                    )
            );
        }
    }
}
