package com.stephenu.gts.trader;

import com.stephenu.gts.commodity.CommodityType;
import com.stephenu.gts.simulation.TradeOpportunity;
import com.stephenu.gts.starsystem.StarSystem;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents an autonomous trader participating in the simulation.
 *
 * Traders maintain their current location, cargo, credits, travel state,
 * and trading strategy as they move goods between star systems.
 */
@Entity
@Table(name = "traders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long credits;

    /**
     * The trader's current location.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "current_system_id")
    private StarSystem currentSystem;


    /**
     * The trade opportunity currently assigned to the trader.
     */
    @OneToOne
    @JoinColumn(name = "trade_opportunity_id")
    private TradeOpportunity currentTrade;

    @Enumerated(EnumType.STRING)
    private StrategyProfile strategyProfile;

    /**
     * The trader's current simulation state.
     */
    @Enumerated(EnumType.STRING)
    private TraderStatus status;

    private Integer cargoCapacity;

    private Integer cargoAmount;

    /**
     * The commodity currently stored in the trader's cargo hold.
     */
    @Enumerated(EnumType.STRING)
    private CommodityType cargoCommodity;

    private Integer travelTicksRemaining;

    private Integer totalTravelTicks;

}
