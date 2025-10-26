package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.representation.compare.TakeoverSummaryDayRepresentation;

public class TakeoverSummaryDayRepresentationConverter {
    private static final String[] WEEKDAYS = {"Söndag","Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag", "Lördag"};


    public static TakeoverSummaryDayRepresentation toRepresentation(int index, PointsInDay pointsInDay) {
        int week = index / 7 + 1;
        String day = WEEKDAYS[index % 7];
        int zones = pointsInDay.getZones();
        int pointsTotal =  pointsInDay.getTotalRounded();
        int pointsTp = pointsInDay.getTakepointRounded();
        int pointsPph = pointsInDay.getPphRounded();
        int zonesLeft = pointsInDay.getZonesLeft();
        int accumulatingPph = pointsInDay.getAccumulatingPph();

        return new TakeoverSummaryDayRepresentation(week, day, zones, pointsTotal, pointsTp, pointsPph, zonesLeft, accumulatingPph);
    }
}
