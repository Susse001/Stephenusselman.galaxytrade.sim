package com.stephenu.gts.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.stephenu.gts.trader.dto.TraderResponse;

import java.util.List;

@RestController
@RequestMapping("/api/traders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TraderController {

    private final TraderService traderService;

    @GetMapping
    public List<TraderResponse> getAllTraders() {
        return traderService.getAllTraders();
    }

    @GetMapping("/{id}")
    public TraderResponse getTraderById(
            @PathVariable Long id
    ) {
        return traderService.getTraderById(id);
    }
}
