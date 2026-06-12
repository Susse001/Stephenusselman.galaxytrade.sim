package com.stephenu.gts.starsystem;
import java.util.List;
import org.springframework.stereotype.Service;

import com.stephenu.gts.starsystem.dto.StarSystemResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StarSystemService {

    private final StarSystemRepository systemRepository;

    public List<StarSystemResponse> getAllSystems() {
        return systemRepository.findAll()
                .stream()
                .map(system -> new StarSystemResponse(
                        system.getId(),
                        system.getName(),
                        system.getXCoordinate(),
                        system.getYCoordinate(),
                        system.getRegion()
                ))
                .toList();
    }

    public StarSystemResponse getSystem(Long id) {
    var system = systemRepository.findById(id)
            .orElseThrow();

    return new StarSystemResponse(
            system.getId(),
            system.getName(),
            system.getXCoordinate(),
            system.getYCoordinate(),
            system.getRegion()
    );
    }
}
