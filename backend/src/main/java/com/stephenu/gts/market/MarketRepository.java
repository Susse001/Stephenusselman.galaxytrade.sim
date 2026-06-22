package com.stephenu.gts.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stephenu.gts.commodity.CommodityType;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    List<Market> findByStarSystemId(Long starSystemId);

    @Query("""
    SELECT m FROM Market m WHERE m.commodity.type = :type
    """)
    List<Market> findByCommodityType(
            @Param("type") CommodityType type
    );

}
