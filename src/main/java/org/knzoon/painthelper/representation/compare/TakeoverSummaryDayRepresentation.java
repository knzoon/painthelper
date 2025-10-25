package org.knzoon.painthelper.representation.compare;

public class TakeoverSummaryDayRepresentation {
    private final Integer week;
    private final String day;
    private final Integer zones;
    private final Integer pointsTotal;
    private final Integer pointsTp;
    private final Integer pointsPph;
    private final Integer zonesLeft;
    private final Integer accumulatingPph;

    public TakeoverSummaryDayRepresentation(Integer week, String day, Integer zones, Integer pointsTotal,
                                            Integer pointsTp, Integer pointsPph, Integer zonesLeft,
                                            Integer accumulatingPph) {
        this.week = week;
        this.day = day;
        this.zones = zones;
        this.pointsTotal = pointsTotal;
        this.pointsTp = pointsTp;
        this.pointsPph = pointsPph;
        this.zonesLeft = zonesLeft;
        this.accumulatingPph = accumulatingPph;
    }

    public Integer getWeek() {
        return week;
    }

    public String getDay() {
        return day;
    }

    public Integer getZones() {
        return zones;
    }

    public Integer getPointsTotal() {
        return pointsTotal;
    }

    public Integer getPointsTp() {
        return pointsTp;
    }

    public Integer getPointsPph() {
        return pointsPph;
    }

    public Integer getZonesLeft() {
        return zonesLeft;
    }

    public Integer getAccumulatingPph() {
        return accumulatingPph;
    }
}
