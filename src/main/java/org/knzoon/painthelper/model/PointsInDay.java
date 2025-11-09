package org.knzoon.painthelper.model;

import java.time.Duration;

public class PointsInDay {

    private final Double total;
    private final Double takepoint;
    private final Double pph;
    private final Integer accumulatingPph;
    private final Integer zones;
    private final Integer zonesLeft;
    private final Duration duration;
    public static final PointsInDay ZERO = new PointsInDay(0.0, 0.0, 0.0, 0, 0, 0, Duration.ZERO);

    private PointsInDay(Double total, Double takepoint, Double pph, Integer accumulatingPph, Integer zones, Integer zonesLeft, Duration duration) {
        this.total = total;
        this.takepoint = takepoint;
        this.pph = pph;
        this.accumulatingPph = accumulatingPph;
        this.zones = zones;
        this.zonesLeft = zonesLeft;
        this.duration = duration;
    }

    public PointsInDay(Double total, Double takepoint, Double pph, Integer accumulatingPph, Integer zonesLeft, Duration duration) {
        this.total = total;
        this.takepoint = takepoint;
        this.pph = pph;
        this.accumulatingPph = accumulatingPph;
        this.zones = 1;
        this.zonesLeft = zonesLeft;
        this.duration = duration;
    }

    public Double getTotal() {
        return total;
    }

    public int getTotalRounded() {
        return (int) Math.round(total);
    }

    public Double getTakepoint() {
        return takepoint;
    }

    public int getTakepointRounded() {
        return (int) Math.round(takepoint);
    }

    public Double getPph() {
        return pph;
    }

    public int getPphRounded() {
        return (int) Math.round(pph);
    }

    public Integer getAccumulatingPph() {
        return accumulatingPph;
    }

    public Integer getZones() {
        return zones;
    }

    public Integer getZonesLeft() {
        return zonesLeft;
    }

    public PointsInDay add(PointsInDay pointsInDay) {
        return new PointsInDay(
                this.total + pointsInDay.getTotal(),
                this.takepoint + pointsInDay.getTakepoint(),
                this.pph + pointsInDay.getPph(),
                this.accumulatingPph + pointsInDay.getAccumulatingPph(),
                this.zones + pointsInDay.getZones(),
                this.zonesLeft + pointsInDay.getZonesLeft(),
                this.duration.plus(pointsInDay.getDuration())
        );
    }

    public boolean hasAccumulatingPph() {
        return accumulatingPph > 0;
    }

    public Duration getDuration() {
        return duration;
    }
}
