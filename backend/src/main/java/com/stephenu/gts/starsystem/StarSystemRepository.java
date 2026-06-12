package com.stephenu.gts.starsystem;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StarSystemRepository extends JpaRepository<StarSystem, Long>  {

        Optional<StarSystem> findByName(String name);
}
