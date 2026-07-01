package com.stephenu.gts.trader;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access for traders.
 */
public interface TraderRepository extends JpaRepository<Trader, Long> {
    
}
