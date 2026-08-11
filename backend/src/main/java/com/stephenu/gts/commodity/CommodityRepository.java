package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access for commodities.
 */
public interface CommodityRepository extends JpaRepository<Commodity, Long> {

        List<Commodity> findByType(CommodityType type);
        List<Commodity> findByTier(int tier);
}
