package com.stephenu.gts.system.dto;

public record SystemResponse (
    Long id,
    String name,
    int xCoordinate,
    int yCoordinate,
    String region
) {}
