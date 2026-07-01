package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.dto.CommodityResponse;

import lombok.RequiredArgsConstructor;

/**
 * Provides services for retrieving commodity data.
 */
@Service
@RequiredArgsConstructor
public class CommodityService {

    private final CommodityRepository commodityRepository;

    /**
     * Returns all commodities.
     *
     * @return A list of all commodities.
     */
    public List<CommodityResponse> getAllCommodities() {

        return commodityRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CommodityResponse mapToResponse(Commodity commodity) {
        return new CommodityResponse(
                commodity.getId(),
                commodity.getType(),
                commodity.getBasePrice()
        );
    }
}
