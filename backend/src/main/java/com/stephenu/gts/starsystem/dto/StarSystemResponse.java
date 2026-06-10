package com.stephenu.gts.system.dto;

public record StarSystemResponse (
    Long id,
    String name,
    int xCoordinate,
    int yCoordinate,
    String region
) {}
