package com.stephenu.gts.market;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarketRepository extends JpaRepository<Market, Long> {

    List<Market> findByStarSystemId(Long starSystemId);

}
