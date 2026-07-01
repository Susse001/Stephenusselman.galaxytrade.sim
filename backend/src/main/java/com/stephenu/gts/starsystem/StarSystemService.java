package com.stephenu.gts.starsystem;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.stephenu.gts.starsystem.dto.StarSystemResponse;

import lombok.RequiredArgsConstructor;

/**
 * Provides services for retrieving star system data.
 */
@Service
@RequiredArgsConstructor
public class StarSystemService {

    private final StarSystemRepository systemRepository;

    /**
     * Returns all star systems.
     *
     * @return A list of all star systems.
     */
    public List<StarSystemResponse> getAllSystems() {
        return systemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Returns the star system with the specified ID.
     *
     * @param id The identifier of the requested star system.
     * @return The requested star system.
     * @throws NoSuchElementException If no star system exists with the specified ID.
     */
    public StarSystemResponse getSystem(Long id) {
        return mapToResponse(
                systemRepository.findById(id)
                        .orElseThrow()
        );
    }

    private StarSystemResponse mapToResponse(StarSystem system) {
        return new StarSystemResponse(
                system.getId(),
                system.getName(),
                system.getXCoordinate(),
                system.getYCoordinate(),
                system.getRegion()
        );
    }
}
