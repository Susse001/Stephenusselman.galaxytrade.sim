package com.stephenu.gts.market;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the database with the initial market for every commodity
 * within each star system.
 *
 * Initial market conditions are derived from the system's economic
 * profile. Starting inventory is intentionally below target inventory
 * to create initial trading opportunities.
 */
@Component
@Order(5)
@RequiredArgsConstructor
public class MarketDataLoader implements CommandLineRunner {

    private final MarketRepository marketRepository;
    private final StarSystemRepository systemRepository;
    private final CommodityRepository commodityRepository;

    /**
	 * Populates the database with initial market data.
	 *
	 * @param args command-line arguments supplied during application startup
	 */
	@Override
	public void run(String... args) {

		if (marketRepository.count() > 0) {
			return;
		}

		List<Commodity> allCommodities =
				commodityRepository.findAll();

		List<Market> markets = new ArrayList<>();

		for (StarSystem system : systemRepository.findAll()) {

			for (Commodity commodity : allCommodities) {

				markets.add(
						new Market(
								system,
								commodity
						)
				);
			}
		}

		marketRepository.saveAll(markets);
	}

}
