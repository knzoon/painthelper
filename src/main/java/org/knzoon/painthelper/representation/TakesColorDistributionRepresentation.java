package org.knzoon.painthelper.representation;

import org.knzoon.painthelper.model.TakesColorDistribution;

public record TakesColorDistributionRepresentation(
        Integer untaken,
        Integer green,
        Integer yellow,
        Integer orange,
        Integer red,
        Integer purple) {

    public TakesColorDistributionRepresentation(TakesColorDistribution colorDistribution) {
        this(colorDistribution.untaken(),
                colorDistribution.green(),
                colorDistribution.yellow(),
                colorDistribution.orange(),
                colorDistribution.red(),
                colorDistribution.purple());
    }
}
