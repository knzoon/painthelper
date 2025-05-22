package org.knzoon.painthelper.model;

public class PointsInDay {

    private final Double total;
    private final Double takepoint;
    private final Double pph;
    public static final PointsInDay ZERO = new PointsInDay(0.0, 0.0, 0.0);

    public PointsInDay(Double total, Double takepoint, Double pph) {
        this.total = total;
        this.takepoint = takepoint;
        this.pph = pph;
    }

    public Double getTotal() {
        return total;
    }

    public Double getTakepoint() {
        return takepoint;
    }

    public Double getPph() {
        return pph;
    }

    public PointsInDay add(PointsInDay pointsInDay) {
        return new PointsInDay(
                this.total + pointsInDay.getTotal(),
                this.takepoint + pointsInDay.getTakepoint(),
                this.pph + pointsInDay.getPph());
    }
}
