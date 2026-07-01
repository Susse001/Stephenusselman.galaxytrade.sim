package com.stephenu.gts.commodity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access for commodities.
 */
public interface CommodityRepository extends JpaRepository<Commodity, Long> {

        Optional<Commodity> findByType(CommodityType type);
}
