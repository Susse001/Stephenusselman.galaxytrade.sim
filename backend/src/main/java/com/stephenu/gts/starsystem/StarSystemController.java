package com.stephenu.gts.starsystem;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stephenu.gts.starsystem.dto.StarSystemResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/systems")
@RequiredArgsConstructor
public class StarSystemController {

    private final StarSystemService systemService;

    @GetMapping
    public List<StarSystemResponse> getSystems() {
        return systemService.getAllSystems();
    }

    @GetMapping("/{id}")
    public StarSystemResponse getSystem(
            @PathVariable Long id
    ) {
        return systemService.getSystem(id);
    }
}
