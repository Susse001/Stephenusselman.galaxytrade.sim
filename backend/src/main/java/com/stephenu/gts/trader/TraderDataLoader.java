package com.stephenu.gts.trader;

import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

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

    private final Random random = new Random();

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
                        createTrader(
                                "Trader " + traderNumber++,
                                system,
                                StrategyProfile.CONSERVATIVE
                        )
                );

                traderRepository.save(
                        createTrader(
                                "Trader " + traderNumber++,
                                system,
                                StrategyProfile.BALANCED
                        )
                );

                traderRepository.save(
                        createTrader(
                                "Trader " + traderNumber++,
                                system,
                                StrategyProfile.AGGRESSIVE
                        )
                );
        }
    }

    private long generateStartingCredits() {

        long[] startingCredits = {
                1_000,
                5_000,
                15_000,
                30_000
        };

        return startingCredits[
                random.nextInt(
                        startingCredits.length
                )
        ];
    }

    private Trader createTrader(
        String name,
        StarSystem system,
        StrategyProfile profile) {

        Trader trader = new Trader();

        trader.setName(name);
        trader.setCurrentSystem(system);
        trader.setCredits(
                generateStartingCredits()
        );
        trader.setCurrentTrade(null);
        trader.setStrategyProfile(profile);
        trader.setStatus(TraderStatus.IDLE);
        trader.setCargoCapacity(100);
        trader.setCargoAmount(0);

        return trader;
        }
}
