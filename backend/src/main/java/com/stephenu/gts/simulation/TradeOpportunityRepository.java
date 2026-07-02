package com.stephenu.gts.simulation;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides database access for trade opportunities.
 */
public interface TradeOpportunityRepository 
 extends JpaRepository<TradeOpportunity, Long> {
}
