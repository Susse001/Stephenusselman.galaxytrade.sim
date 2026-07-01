package com.stephenu.gts.trader;

import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with the initial set of traders.
 *
 * Traders are distributed across star systems and assigned randomized
 * starting credits and strategy profiles.
 */
@Component
@Order(4)
@RequiredArgsConstructor
public class TraderDataLoader
        implements CommandLineRunner {

    private final TraderRepository traderRepository;
    private final StarSystemRepository starSystemRepository;

    private final Random random = new Random();

    /**
	 * Populates the database with initial trader data.
	 *
	 * @param args Command-line arguments supplied during application startup.
	 */
    @Override
    public void run(String... args) {

        if (traderRepository.count() > 0) {
                return;
        }

        List<StarSystem> systems =
                starSystemRepository.findAll();

        StrategyProfile[] profiles = {
        StrategyProfile.CONSERVATIVE,
        StrategyProfile.BALANCED,
        StrategyProfile.AGGRESSIVE
        };

		List<Trader> traders = new ArrayList<>();

        for (int i = 0; i < 60; i++) {

        StarSystem system =
                systems.get(
                        i % systems.size()
                );

        StrategyProfile profile =
                profiles[
                        i % profiles.length
                ];

        traders.add(
                createTrader(
                        "Trader " + (i + 1),
                        system,
                        profile
                )
            );
        }
		traderRepository.saveAll(traders);
    }

    private long generateStartingCredits() {

        long[] startingCredits = {
                2_000,
                5_000,
                10_000,
                20_000,
                35_000,
                55_000,
                80_000
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
				trader.setCargoCommodity(null);

                return trader;
        }
}
