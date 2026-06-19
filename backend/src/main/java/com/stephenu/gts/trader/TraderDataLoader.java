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

                Trader conservative = new Trader();

                conservative.setName("Trader " + traderNumber++);
                conservative.setCurrentSystem(system);
                conservative.setCredits((long) 10000);
                conservative.setStrategyProfile(
                        StrategyProfile.CONSERVATIVE);
                conservative.setStatus(TraderStatus.IDLE);
                conservative.setCargoCapacity(100);
                conservative.setCargoAmount(0);

                traderRepository.save(conservative);

                Trader aggressive = new Trader();

                aggressive.setName("Trader " + traderNumber++);
                aggressive.setCurrentSystem(system);
                aggressive.setCredits((long) 10000);
                aggressive.setStrategyProfile(
                        StrategyProfile.AGGRESSIVE);
                aggressive.setStatus(TraderStatus.IDLE);
                aggressive.setCargoCapacity(100);
                aggressive.setCargoAmount(0);

                traderRepository.save(aggressive);
                }
}
