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

    @ManyToOne(optional = false)
    @JoinColumn(name = "current_system_id")
    private StarSystem currentSystem;

    @ManyToOne
    @JoinColumn(name = "target_system_id")
    private StarSystem targetSystem;

    @OneToOne
    @JoinColumn(name = "trade_opportunity_id")
    private TradeOpportunity currentTrade;

    @Enumerated(EnumType.STRING)
    private StrategyProfile strategyProfile;

    @Enumerated(EnumType.STRING)
    private TraderStatus status;

    private Integer cargoCapacity;

    private Integer cargoAmount;

    @Enumerated(EnumType.STRING)
    private CommodityType cargoCommodity;

}
