package com.stephenu.gts.trader;

/**
 * Defines the trading behavior used when evaluating trade opportunities.
 */
public enum StrategyProfile {
    /** Prioritizes balanced profit and travel distance. */
    BALANCED,
    /** Prefers higher-profit routes despite longer travel times. */
    AGGRESSIVE,
    /** Prefers shorter, lower-risk trade routes. */
    CONSERVATIVE
}
