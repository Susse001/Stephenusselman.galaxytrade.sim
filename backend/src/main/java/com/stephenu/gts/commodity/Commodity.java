package com.stephenu.gts.commodity;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a tradeable commodity within the simulation.
 *
 * Each commodity defines a unique type and a base price used when
 * generating market prices.
 */
@Entity
@Table(name = "commodities")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class Commodity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The unique type represented by this commodity.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private CommodityType type;

    @Column(nullable = false)
    private int basePrice;

    @Column(nullable = false)
    private int tier;

    private ProductionRecipe productionRecipe;

    public Commodity(
            Long id,
            CommodityType type,
            int basePrice,
            int tier)   {
                this.id = id;
                this.type = type;
                this.basePrice = basePrice;
                this.tier = tier;
            }

    public void calculateTier1GoodTotals() {

        if (productionRecipe == null) {
            return;
        }

        productionRecipe.setTier1GoodTotals(
                calculateTier1Requirements()
        );
    }

    /**
     * Recursively resolves this commodity's inputs into Tier 1 requirements.
     *
     * @return Map containing the total Tier 1 input required.
     */
    private Map<Commodity, Double> calculateTier1Requirements() {

        Map<Commodity, Double> totals =
                new HashMap<>();

        if (productionRecipe == null) {
            totals.put(this, 1.0);
            return totals;
        }

        for (Map.Entry<Commodity, Double> entry :
                productionRecipe.getInputs().entrySet()) {

            Commodity input = entry.getKey();
            double quantity = entry.getValue();

            Map<Commodity, Double> inputTotals =
                    input.calculateTier1Requirements();

            inputTotals.forEach((tier1Commodity, amount) ->
                    totals.merge(
                            tier1Commodity,
                            amount * quantity,
                            Double::sum
                    )
            );
        }

        return totals;
    }
}
