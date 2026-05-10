package org.knzoon.painthelper.representation.compare;

public record TakeoverSummaryDayRepresentation(
        Integer week,
        String day,
        Integer zones,
        Integer pointsTotal,
        Integer pointsTp,
        Integer pointsPph,
        Integer zonesLeft,
        Integer accumulatingPph) {
}
