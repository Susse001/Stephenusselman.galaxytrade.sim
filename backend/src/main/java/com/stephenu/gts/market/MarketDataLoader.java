package com.stephenu.gts.market;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityRepository;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.Region;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemRepository;

import lombok.RequiredArgsConstructor;

/**
 * Seeds the database with procedurally generated market data.
 *
 * Each star system is assigned a set of export and import markets based
 * on its galactic region. Initial prices, supply, and demand values are
 * generated to create regional trade opportunities.
 */
@Component
@Order(4)
@RequiredArgsConstructor
public class MarketDataLoader implements CommandLineRunner {

	private final MarketRepository marketRepository;
	private final StarSystemRepository systemRepository;
	private final CommodityRepository commodityRepository;

	private static final List<CommodityType> TIER_1 = List.of(
		CommodityType.FOOD,
		CommodityType.WATER,
		CommodityType.ORE,
		CommodityType.RARE_METALS,
		CommodityType.ORGANICS
	);

	private static final List<CommodityType> TIER_2 = List.of(
			CommodityType.CHEMICAL_COMPOUNDS,
			CommodityType.REFINED_METALS,
			CommodityType.ADVANCED_COMPONENTS
	);

	private static final List<CommodityType> TIER_3 = List.of(
			CommodityType.MEDICINE,
			CommodityType.INDUSTRIAL_PARTS,
			CommodityType.ELECTRONICS
	);

	private final Random random = new Random();


	/**
	 * Populates the database with initial market data.
	 *
	 * @param args Command-line arguments supplied during application startup.
	 */
	@Override
	public void run(String... args) {

		if (marketRepository.count() > 0) {
			return;
		}

		List<Commodity> allCommodities =
				commodityRepository.findAll();

		Map<CommodityType, Commodity> commodityMap =
			allCommodities
					.stream()
					.collect(Collectors.toMap(
								Commodity::getType,
								Function.identity()
					));


		List<Market> markets = new ArrayList<>();

		for (StarSystem system : systemRepository.findAll()) {


			Set<Commodity> exports =
					generateExports(
						system,
						commodityMap
					);

			Set<Commodity> imports =
					generateImports(
						exports,
						allCommodities
					);

			createMarkets(system, imports, exports, markets);
		}

		marketRepository.saveAll(markets);
	}

	private void createMarkets(
		StarSystem system, 
		Set<Commodity> imports, 
		Set<Commodity> exports, 
		List<Market> markets) {

			exports.forEach(c ->
					markets.add(
							createExportMarket(
									system,
									c
							)
					)
			);

			imports.forEach(c ->
					markets.add(
							createImportMarket(
									system,
									c
							)
					)
			);

		}

	/**
	 * Builds a weighted commodity pool for the specified region.
	 *
	 * Commodity weighting influences which commodities are selected as
	 * regional exports while still allowing some randomness.
	 */
	private List<Commodity> buildExportPool(
		Region region,
	    Map<CommodityType, Commodity> commodityMap) {

		List<Commodity> pool = new ArrayList<>();

		switch (region) {

			case CORE -> {
				addCopies(pool, TIER_1, 2, commodityMap);
				addCopies(pool, TIER_2, 4, commodityMap);
				addCopies(pool, TIER_3, 6, commodityMap);
				addCopies(
						pool,
						List.of(CommodityType.LUXURY_GOODS),
						2,
						commodityMap
				);
			}
			case INNER_RIM -> {
				addCopies(pool, TIER_1, 4, commodityMap);
				addCopies(pool, TIER_2, 4, commodityMap);
				addCopies(pool, TIER_3, 2, commodityMap);
				addCopies(
						pool,
						List.of(CommodityType.LUXURY_GOODS),
						1,
						commodityMap
				);
			}
			case OUTER_RIM -> {
				addCopies(pool, TIER_1, 6, commodityMap);
				addCopies(pool, TIER_2, 3, commodityMap);
				addCopies(pool, TIER_3, 1, commodityMap);
				addCopies(
						pool,
						List.of(CommodityType.LUXURY_GOODS),
						1,
						commodityMap
				);
			}
		}

		Collections.shuffle(pool);

		return pool;
	}

	/**
	 * Selects the commodities exported by a star system.
	 *
	 * Export commodities are chosen from a weighted pool based on the
	 * system's galactic region.
	 */
	private Set<Commodity> generateExports(
		StarSystem system,
	    Map<CommodityType, Commodity> commodityMap) 
	    {

		List<Commodity> exportPool =
        buildExportPool(system.getRegion(), commodityMap);

		return drawCommodities(exportPool, 3);
	}

	/**
	 * Selects the commodities imported by a star system.
	 *
	 * Imports are randomly chosen from commodities that were not selected
	 * as exports.
	 */
	private Set<Commodity> generateImports(
		Set<Commodity> exports,
		List<Commodity> allCommodities) {

		List<Commodity> candidates =
			new ArrayList<>(
					allCommodities.stream()
							.filter(c ->
								!exports.contains(c))
							.toList()
			);

		Collections.shuffle(candidates);

		return new HashSet<>(
				candidates.subList(0, 3)
		);
	}

	/**
	 * Creates an export market for the specified commodity.
	 */
	private Market createExportMarket(
		StarSystem system,
		Commodity commodity) {

		double modifier =
				0.70 + random.nextDouble() * 0.20;

		return new Market(
				system,
				commodity,
				(int)(commodity.getBasePrice() * modifier),
				1000,
				200
		);
	}

	/**
	 * Creates an import market for the specified commodity.
	 */
	private Market createImportMarket(
		StarSystem system,
		Commodity commodity) {

		double modifier =
				1.10 + random.nextDouble() * 0.30;

		return new Market(
				system,
				commodity,
				(int)(commodity.getBasePrice() * modifier),
				200,
				1000
		);
	}

	private void addCopies(
		List<Commodity> pool,
		List<CommodityType> types,
		int copies,
	    Map<CommodityType, Commodity> commodityMap) {

		for (CommodityType type : types) {

			for (int i = 0; i < copies; i++) {
				pool.add(commodityMap.get(type));
			}
		}
	}

	/**
	 * Draws a unique set of commodities from a weighted pool.
	 *
	 * The pool is reshuffled as needed to preserve weighting while ensuring
	 * the requested number of unique commodities is returned.
	 */
	private Set<Commodity> drawCommodities(
		List<Commodity> pool,
		int count) {

		Set<Commodity> result =
				new HashSet<>();

		int index = 0;

		while (result.size() < count) {

			if (index >= pool.size()) {
				Collections.shuffle(pool);
				index = 0;
			}

			result.add(pool.get(index++));
		}

		return result;
	}
}
