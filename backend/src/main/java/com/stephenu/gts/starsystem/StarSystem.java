package com.stephenu.gts.starsystem;

import java.util.ArrayList;
import java.util.List;

import com.stephenu.gts.planet.Planet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a star system within the simulated galaxy.
 *
 * Each star system stores its location and region and serves as the
 * foundation for local markets and trader movement.
 */
@Entity
@Table(name = "systems")
@Getter
@Setter
@NoArgsConstructor
public class StarSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int xCoordinate;

    private int yCoordinate;

    /**
     * Procedurally assigned galactic region.
     */
    private Region region;

    @OneToMany(
        mappedBy = "starSystem",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<Planet> planets = new ArrayList<>();

    /**
     * The economic capabilities and limitatations of all the planets in this star system.
     */
    private StarSystemEconomicProfile economicProfile;

    public StarSystem(
        Long id,
        String name,
        Integer xCoordinate,
        Integer yCoordinate,
        Region region) {

        this.id = id;
        this.name = name;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.region = region;
    }

    public void addPlanet(Planet planet) {
        planets.add(planet);
        planet.setStarSystem(this);
    }

    public void removePlanet(Planet planet) {
        planets.remove(planet);
        planet.setStarSystem(null);
    }
}
