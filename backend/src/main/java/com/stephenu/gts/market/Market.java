package com.stephenu.gts.market;

import com.stephenu.gts.commodity.Commodity;
import com.stephenu.gts.starsystem.StarSystem;

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
     * Current amount physically stored in this market.
     */
    private Integer inventory;

    /**
     * Desired inventory level for this commodity.
     *
     * Used to calculate scarcity and price.
     */
    private Integer targetInventory;

    public Market(
            StarSystem starSystem,
            Commodity commodity,
            Integer price,
            Integer inventory,
            Integer targetInventory
    ) {
        this.starSystem = starSystem;
        this.commodity = commodity;
        this.price = price;
        this.inventory = inventory;
        this.targetInventory = targetInventory;
    }
}
