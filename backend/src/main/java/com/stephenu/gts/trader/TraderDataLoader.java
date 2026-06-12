package com.stephenu.gts.trader;

import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraderDataLoader implements CommandLineRunner {

    private final TraderRepository traderRepository;
    private final StarSystemRepository starSystemRepository;

    @Override
    public void run(String... args) {

        if (traderRepository.count() > 0) {
            return;
        }

        StarSystem titanGate =
                starSystemRepository.findByName("Titan Gate")
                        .orElseThrow();

        StarSystem novaPrime =
                starSystemRepository.findByName("Nova Prime")
                        .orElseThrow();

        traderRepository.save(
                new Trader(
                        null,
                        "Atlas Trading Company",
                        titanGate,
                        10000,
                        StrategyProfile.BALANCED
                )
        );

        traderRepository.save(
                new Trader(
                        null,
                        "Helix Freight",
                        novaPrime,
                        15000,
                        StrategyProfile.AGGRESSIVE
                )
        );

        traderRepository.save(
                new Trader(
                        null,
                        "Frontier Logistics",
                        titanGate,
                        8000,
                        StrategyProfile.CONSERVATIVE
                )
        );
    }
}
