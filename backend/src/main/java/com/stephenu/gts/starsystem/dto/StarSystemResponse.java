package com.stephenu.gts.starsystem.dto;

import com.stephenu.gts.starsystem.Region;

public record StarSystemResponse (
    Long id,
    String name,
    int xCoordinate,
    int yCoordinate,
    Region region
) {}
