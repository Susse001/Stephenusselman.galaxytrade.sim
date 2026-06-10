package com.stephenu.gts.commodity;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stephenu.gts.commodity.dto.CommodityResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommodityService {

    private final CommodityRepository commodityRepository;

    public List<CommodityResponse> getAllCommodities() {

        return commodityRepository.findAll()
                .stream()
                .map(commodity ->
                        new CommodityResponse(
                                commodity.getId(),
                                commodity.getType(),
                                commodity.getBasePrice()
                        ))
                .toList();
    }
}
