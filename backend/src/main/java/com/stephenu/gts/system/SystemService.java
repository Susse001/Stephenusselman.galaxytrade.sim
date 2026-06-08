package com.stephenu.gts.system;
import com.stephenu.gts.system.dto.SystemResponse;

import java.util.List;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final SystemRepository systemRepository;

    public List<SystemResponse> getAllSystems() {
        return systemRepository.findAll()
                .stream()
                .map(system -> new SystemResponse(
                        system.getId(),
                        system.getName(),
                        system.getXCoordinate(),
                        system.getYCoordinate(),
                        system.getRegion()
                ))
                .toList();
    }

    public SystemResponse getSystem(Long id) {
    var system = systemRepository.findById(id)
            .orElseThrow();

    return new SystemResponse(
            system.getId(),
            system.getName(),
            system.getXCoordinate(),
            system.getYCoordinate(),
            system.getRegion()
    );
    }
}
