package com.stephenu.gts.system;
import com.stephenu.gts.system.dto.StarSystemResponse;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final SystemRepository systemRepository;

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
