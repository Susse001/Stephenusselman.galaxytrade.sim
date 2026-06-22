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

    @ManyToOne(optional = false)
    @JoinColumn(name = "star_system_id")
    private StarSystem starSystem;

    @ManyToOne(optional = false)
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;

    private Integer price;

    private Integer supply;

    private Integer demand;

    public Market(
            StarSystem starSystem,
            Commodity commodity,
            Integer price,
            Integer supply,
            Integer demand
    ) {
        this.starSystem = starSystem;
        this.commodity = commodity;
        this.price = price;
        this.supply = supply;
        this.demand = demand;
    }
}
