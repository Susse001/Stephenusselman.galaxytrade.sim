package com.stephenu.gts.planet;

import com.stephenu.gts.commodity.Commodity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents the natural abundance of a commodity on a planet.
 *
 * Resources describe what can be extracted or harvested from the planet.
 * They do not represent stored inventory or production.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "planet_resources",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "planet_id",
                "commodity_id"
            }
        )
    }
)
public class PlanetResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Planet containing this resource.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "planet_id")
    private Planet planet;

    /**
     * Resource represented by this deposit.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "commodity_id")
    private Commodity commodity;

    /**
     * Relative abundance of the resource.
     */
    @Enumerated(EnumType.STRING)
    private ResourceLevel abundance;

    public PlanetResource(
            Planet planet,
            Commodity commodity,
            ResourceLevel abundance
    ) {
        this.planet = planet;
        this.commodity = commodity;
        this.abundance = abundance;
    }
}
