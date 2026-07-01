package com.stephenu.gts.trader;

/**
 * Represents the current state of a trader within the simulation.
 */
public enum TraderStatus {
    /** Traveling to the source market. */
    TRAVELING_TO_BUY,
    /** Purchasing cargo at the source market. */
    BUYING,
    /** Traveling to the destination market. */
    TRAVELING_TO_SELL,
    /** Selling cargo at the destination market. */
    SELLING,
    /** Waiting for a new trade opportunity. */
    IDLE
}
