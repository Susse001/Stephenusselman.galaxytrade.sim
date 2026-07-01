package com.stephenu.gts.starsystem.dto;

import com.stephenu.gts.starsystem.Region;

/**
 * Represents the API response for a star system.
 *
 * @param id The unique identifier of the star system.
 * @param name The name of the star system.
 * @param xCoordinate The horizontal map coordinate.
 * @param yCoordinate The vertical map coordinate.
 * @param region The galactic region containing the star system.
 */
public record StarSystemResponse (
    Long id,
    String name,
    int xCoordinate,
    int yCoordinate,
    Region region
) {}
