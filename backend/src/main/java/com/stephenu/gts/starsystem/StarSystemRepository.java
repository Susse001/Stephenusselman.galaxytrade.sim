package com.stephenu.gts.starsystem;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access for star systems.
 */
public interface StarSystemRepository extends JpaRepository<StarSystem, Long>  {

        Optional<StarSystem> findByName(String name);
}
