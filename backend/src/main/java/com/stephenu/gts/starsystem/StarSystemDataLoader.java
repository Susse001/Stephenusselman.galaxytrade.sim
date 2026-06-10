package com.stephenu.gts.system;

import java.util.List;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SystemDataLoader {

    private final SystemRepository systemRepository;

    @PostConstruct
    public void loadData() {

        if (systemRepository.count() > 0) {
            return;
        }

        systemRepository.saveAll(List.of(
                new StarSystem(null, "Titan Gate", 100, 150, "Core"),
                new StarSystem(null, "Nova Prime", 300, 120, "Core"),
                new StarSystem(null, "Helios Reach", 500, 250, "Mid Rim"),
                new StarSystem(null, "Iron Bastion", 200, 400, "Outer Rim"),
                new StarSystem(null, "Kepler Junction", 650, 350, "Outer Rim")
        ));
    }
}