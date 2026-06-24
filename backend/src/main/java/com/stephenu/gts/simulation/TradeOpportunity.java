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

    @Enumerated(EnumType.STRING)
    private CommodityType commodity;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buy_system_id")
    private StarSystem buySystem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sell_system_id")
    private StarSystem sellSystem;

    private Integer expectedProfitPerUnit;

    private Integer buyPrice;

    private Integer sellPrice;

}
