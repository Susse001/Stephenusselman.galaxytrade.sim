package com.stephenu.gts.starsystem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Provides database access for star systems.
 */
public interface StarSystemRepository extends JpaRepository<StarSystem, Long>  {

    Optional<StarSystem> findByName(String name);

    @Query("""
    SELECT DISTINCT s
    FROM StarSystem s
    LEFT JOIN FETCH s.planets
    """)
    List<StarSystem> findAllWithPlanets();
}
