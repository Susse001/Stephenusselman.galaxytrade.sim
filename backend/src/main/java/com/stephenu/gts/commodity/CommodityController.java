package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stephenu.gts.commodity.dto.CommodityResponse;

import lombok.RequiredArgsConstructor;

/**
 * Exposes REST endpoints for retrieving commodity data.
 */
@RestController
@RequestMapping("/api/commodities")
@RequiredArgsConstructor
public class CommodityController {

    private final CommodityService commodityService;

    /**
     * Returns all commodities.
     *
     * @return A list of all commodities.
     */
    @GetMapping
    public List<CommodityResponse> getCommodities() {
        return commodityService.getAllCommodities();
    }
}
