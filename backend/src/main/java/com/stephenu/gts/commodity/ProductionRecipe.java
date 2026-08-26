package com.stephenu.gts.commodity;

import java.util.EnumMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductionRecipe {

    private double outputAmount;

    private Map<CommodityType, Double> inputs =
            new EnumMap<>(CommodityType.class);

    public ProductionRecipe(
            double outputAmount,
            Map<CommodityType, Double> inputs) {

        this.outputAmount = outputAmount;
        this.inputs = inputs;
    }
}
