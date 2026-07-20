package com.stephenu.gts.planet;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Provides database access for planets.
 */
public interface PlanetRepository extends JpaRepository<Planet, Long> {

    Optional<Planet> findByName(String name);
}
