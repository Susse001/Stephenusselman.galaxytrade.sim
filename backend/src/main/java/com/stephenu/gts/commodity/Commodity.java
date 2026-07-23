package com.stephenu.gts.commodity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a tradeable commodity within the simulation.
 *
 * Each commodity defines a unique type and a base price used when
 * generating market prices.
 */
@Entity
@Table(name = "commodities")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique type represented by this commodity.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CommodityType type;

    @Column(nullable = false)
    private int basePrice;

    @Column(nullable = false)
    private int tier;

}
