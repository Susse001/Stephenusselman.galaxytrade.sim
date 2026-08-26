package com.stephenu.gts.commodity;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductionRecipe {

    private double outputAmount;

    private Map<Commodity, Double> inputs =
            new HashMap<>();

    private Map<Commodity, Double> tier1GoodTotals =
            new HashMap<>();

    public ProductionRecipe(
            double outputAmount,
            Map<Commodity, Double> inputs) {

        this.outputAmount = outputAmount;
        this.inputs = inputs;
    }
}
