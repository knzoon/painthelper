package org.knzoon.painthelper.representation.compare;

public record RouteTotalRepresentation(
        Integer takes,
        String startTime,
        String timeSpent,
        Integer pointsTotal,
        Integer pointsTp,
        Integer pointsPph) {
}
