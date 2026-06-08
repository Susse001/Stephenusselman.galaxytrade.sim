package com.stephenu.gts.system;
import com.stephenu.gts.system.dto.SystemResponse;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/systems")
@RequiredArgsConstructor
public class SystemController {

    private final SystemService systemService;

    @GetMapping
    public List<SystemResponse> getSystems() {
        return systemService.getAllSystems();
    }

    @GetMapping("/{id}")
    public SystemResponse getSystem(
            @PathVariable Long id
    ) {
        return systemService.getSystem(id);
    }
}
