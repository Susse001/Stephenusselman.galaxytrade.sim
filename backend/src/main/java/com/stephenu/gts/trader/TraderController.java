package com.stephenu.gts.trader;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.stephenu.gts.trader.dto.TraderResponse;

import java.util.List;

/**
 * Exposes REST endpoints for retrieving trader data.
 */
@RestController
@RequestMapping("/api/traders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TraderController {

    private final TraderService traderService;

    /**
     * Returns all traders.
     *
     * @return A list of all traders.
     */
    @GetMapping
    public List<TraderResponse> getAllTraders() {
        return traderService.getAllTraders();
    }

    /**
     * Returns the requested trader.
     *
     * @param id The identifier of the requested trader.
     * @return The requested trader.
     */
    @GetMapping("/{id}")
    public TraderResponse getTraderById(
            @PathVariable Long id
    ) {
        return traderService.getTraderById(id);
    }
}
