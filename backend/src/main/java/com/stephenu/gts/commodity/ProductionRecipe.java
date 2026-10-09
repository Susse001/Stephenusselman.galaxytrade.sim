package com.stephenu.gts.commodity;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyJoinColumn;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ProductionRecipe {

    private Double outputAmount = 1.0;

    @ElementCollection
    @CollectionTable(
            name = "commodity_recipe_inputs",
            joinColumns = @JoinColumn(name = "commodity_id")
    )
    @MapKeyJoinColumn(name = "input_commodity_id")
    @Column(name = "quantity")
    private Map<Commodity, Double> inputs =
            new HashMap<>();

    @ElementCollection
    @CollectionTable(
            name = "commodity_recipe_tier1_totals",
            joinColumns = @JoinColumn(name = "commodity_id")
    )
    @MapKeyJoinColumn(name = "tier1_commodity_id")
    @Column(name = "quantity")
    private Map<Commodity, Double> tier1GoodTotals =
            new HashMap<>();

    public ProductionRecipe(
            double outputAmount,
            Map<Commodity, Double> inputs) {

        this.outputAmount = outputAmount;
        this.inputs = inputs;
    }
}
