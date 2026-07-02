package com.stephenu.gts.simulation;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.starsystem.StarSystem;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a profitable trade route identified by the simulation.
 *
 * A trade opportunity links a source market, destination market, and
 * commodity, allowing traders to execute a complete buy-and-sell route
 * without performing market analysis themselves.
 */
@Entity
@Table(name = "trade_opportunities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TradeOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The commodity traded along this route.
     */
    @Enumerated(EnumType.STRING)
    private CommodityType commodity;

    /**
     * The star system where the commodity should be purchased.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "buy_system_id")
    private StarSystem buySystem;

    /**
     * The star system where the commodity should be sold.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "sell_system_id")
    private StarSystem sellSystem;

    /**
     * The estimated profit earned per unit transported.
     */
    private Integer expectedProfitPerUnit;

    private Integer buyPrice;

    private Integer sellPrice;

}
