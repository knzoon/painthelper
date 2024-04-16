package org.knzoon.painthelper.representation.compare;

public class GraphDatapointRepresentation {
    private final String day;
    private final Integer points;

    public GraphDatapointRepresentation(String day, Integer points) {
        this.day = day;
        this.points = points;
    }

    public String getDay() {
        return day;
    }

    public Integer getPoints() {
        return points;
    }
}
