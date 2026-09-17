package com.stephenu.gts.market;

import java.util.Random;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.StarSystem;
import com.stephenu.gts.starsystem.StarSystemEconomicProfile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Represents the market data for a commodity within a specific star system.
 *
 * Each market stores the current trading conditions for a single
 * commodity at a single star system.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "markets",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "star_system_id",
                "commodity_id"
            }
        )
    }
)
public class Market {

    private final Random random = new Random();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The star system that owns this market.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "star_system_id")
    private StarSystem starSystem;

    /**
     * The commodity traded by this market.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;

    /**
     * Current trading price.
     *
     * Recalculated every simulation tick from inventory.
     */
    private Integer price;

    /**
     * Current amount physically stored within the system's market
     * for this commodity.
     */
    private Integer inventory;

    /**
     * Desired system-wide inventory level for this commodity.
     *
     * Used to determine commodity scarcity and influence price.
     */
    private Integer targetInventory;

    public Market(
            StarSystem starSystem,
            Commodity commodity
    ) {
        this.starSystem = starSystem;
        this.commodity = commodity;
        StarSystemEconomicProfile economicProfile =
            starSystem.getEconomicProfile();

        CommodityType type = commodity.getType();

        double production =
                economicProfile.getExtractionCapacity()
                        .getOrDefault(type, 0.0)
                + economicProfile.getManufacturing()
                        .getOrDefault(type, 0.0);

        double consumption =
                economicProfile.getConsumption()
                        .getOrDefault(type, 0.0);

        this.targetInventory =
                calculateTargetInventory(
                        production,
                        consumption
                );

        this.inventory =
                generateStartingInventory(targetInventory);

        this.price =
                calculatePrice(
                        commodity.getBasePrice(),
                        inventory,
                        targetInventory
                );
    }

    /**
     * Calculates the desired inventory for a commodity based on
     * thirty ticks of the system's larger economic flow.
     *
     * @param productionPerTick system production per tick
     * @param consumptionPerTick system consumption per tick
     * @return desired inventory level
     */
    private int calculateTargetInventory(
            double productionPerTick,
            double consumptionPerTick) {

        double throughput =
                Math.max(
                        productionPerTick,
                        consumptionPerTick
                );

        return (int) Math.ceil(throughput * 30);
    }

    /**
     * Calculates the current market price from inventory relative
     * to the desired inventory level.
     *
     * @param basePrice commodity base price
     * @param inventory current inventory
     * @param targetInventory desired inventory level
     * @return calculated market price
     */
    public int calculatePrice(
            int basePrice,
            int inventory,
            int targetInventory) {

        if (targetInventory <= 0) {
            return basePrice;
        }

        if (inventory <= 0) {
            return basePrice * 2;
        }

        double inventoryRatio =
                (double) targetInventory / inventory;

        double priceMultiplier =
                Math.sqrt(inventoryRatio);

        priceMultiplier =
                Math.max(
                        0.5,
                        Math.min(2.0, priceMultiplier)
                );

        return (int) Math.round(
                basePrice * priceMultiplier
        );
    }

    /**
     * Generates an initial inventory between 35% and 65% of the
     * desired inventory.
     *
     * @param targetInventory desired inventory level
     * @return starting inventory
     */
    private int generateStartingInventory(
            int targetInventory) {

        double modifier =
                0.35 + random.nextDouble() * 0.30;

        return (int) Math.round(
                targetInventory * modifier
        );
    }
}
