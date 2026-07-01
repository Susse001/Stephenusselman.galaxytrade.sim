package com.stephenu.gts.starsystem;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
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
}
