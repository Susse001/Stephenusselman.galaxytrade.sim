package com.stephenu.gts.starsystem.dto;

public record StarSystemResponse (
    Long id,
    String name,
    int xCoordinate,
    int yCoordinate,
    String region
) {}
