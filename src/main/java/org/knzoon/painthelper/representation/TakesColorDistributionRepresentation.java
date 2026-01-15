package org.knzoon.painthelper.representation;

import org.knzoon.painthelper.model.TakesColorDistribution;

public class TakesColorDistributionRepresentation {
    private final Integer untaken;
    private final Integer green;
    private final Integer yellow;
    private final Integer orange;
    private final Integer red;
    private final Integer purple;

    public TakesColorDistributionRepresentation(Integer untaken,
                                                Integer green,
                                                Integer yellow,
                                                Integer orange,
                                                Integer red,
                                                Integer purple) {
        this.untaken = untaken;
        this.green = green;
        this.yellow = yellow;
        this.orange = orange;
        this.red = red;
        this.purple = purple;
    }

    public TakesColorDistributionRepresentation(TakesColorDistribution colorDistribution) {
        this.untaken = colorDistribution.untaken();
        this.green = colorDistribution.green();
        this.yellow = colorDistribution.yellow();
        this.orange = colorDistribution.orange();
        this.red = colorDistribution.red();
        this.purple = colorDistribution.purple();
    }

    public Integer getUntaken() {
        return untaken;
    }

    public Integer getGreen() {
        return green;
    }

    public Integer getYellow() {
        return yellow;
    }

    public Integer getOrange() {
        return orange;
    }

    public Integer getRed() {
        return red;
    }

    public Integer getPurple() {
        return purple;
    }
}
