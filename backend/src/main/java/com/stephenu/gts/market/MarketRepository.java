package com.stephenu.gts.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stephenu.gts.commodity.CommodityType;

import java.util.List;
import java.util.Optional;

/**
 * Provides database access for markets.
 */
public interface MarketRepository extends JpaRepository<Market, Long> {

    List<Market> findByStarSystemId(Long starSystemId);

    @Query("""
    SELECT m FROM Market m WHERE m.commodity.type = :type
    """)
    List<Market> findByCommodityType(
            @Param("type") CommodityType type
    );

    /**
     * Finds the market for a commodity within a star system.
     *
     * @param starSystemId The star system ID.
     * @param commodityType The commodity type.
     * @return The matching market, if one exists.
     */
    @Query("""
        SELECT m FROM Market m
        WHERE m.starSystem.id = :starSystemId
        AND m.commodity.type = :commodityType
        """)
    Optional<Market> findByStarSystemIdAndCommodityType(
            @Param("starSystemId") Long starSystemId,
            @Param("commodityType") CommodityType commodityType
    );

}
