package org.knzoon.painthelper.representation;

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
