package com.stephenu.gts.planet;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "planets")
public class Planet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Parent star system.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "star_system_id")
    private StarSystem starSystem;

    /**
     * Display name.
     */
    private String name;

    /**
     * Dominant planetary environment.
     */
    @Enumerated(EnumType.STRING)
    private PlanetType planetType;

    /**
     * Approximate population.
     */
    @Enumerated(EnumType.STRING)
    private PopulationLevel population;

    /**
     * Overall technological and industrial development.
     */
    @Enumerated(EnumType.STRING)
    private DevelopmentLevel development;

    /**
     * Transportation and industrial infrastructure.
     */
    @Enumerated(EnumType.STRING)
    private InfrastructureLevel infrastructure;
}