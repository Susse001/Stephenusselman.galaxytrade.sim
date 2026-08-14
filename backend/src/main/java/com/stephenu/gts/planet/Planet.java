package com.stephenu.gts.planet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.stephenu.gts.starsystem.StarSystem;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
     * The position of the planet in system in reference to the center
     */
    private Integer orbitalOrder;

    /**
     * 
     */
    @Enumerated(EnumType.STRING)
    private OrbitZone orbitZone;

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

    /**
     * Natural resource deposits available on the planet.
     */
    @OneToMany(
        mappedBy = "planet",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<PlanetResource> resources = new ArrayList<>();

    /**
     * Planet's calculated extraction and manufacturing potential.
     */
    @Transient
    private PlanetProductionProfile productionProfile;

    /**
     * Unique planetary features.
     */
    @ElementCollection(targetClass = PlanetFeature.class)
    @CollectionTable(
        name = "planet_features",
        joinColumns = @JoinColumn(name = "planet_id")
    )
    @Column(name = "feature")
    @Enumerated(EnumType.STRING)
    private Set<PlanetFeature> features = new HashSet<>();

    /**
     * 
     * @param feature The planetary feature you want to check for
     * @return A boolean indicating if the given planet contains the feature.
     */
    public boolean hasFeature(
        PlanetFeature feature) 
    {
        return this.getFeatures().contains(feature);
    }
}