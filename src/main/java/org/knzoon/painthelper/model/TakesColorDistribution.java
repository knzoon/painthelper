package org.knzoon.painthelper.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TakesColorDistribution {
    private final int untaken;
    private final Map<Color, List<UniqueZoneView>> colorDistribution;

    public TakesColorDistribution(int untaken, List<? extends UniqueZoneView> uniqueZones) {
        this.untaken = untaken;
        this.colorDistribution = uniqueZones.stream().collect(Collectors.groupingBy(UniqueZoneView::getColor));
    }

    public int untaken() {
        return untaken;
    }

    public int green() {
        return colorDistribution.getOrDefault(Color.GREEN, List.of()).size();
    }

    public int yellow() {
        return colorDistribution.getOrDefault(Color.YELLOW, List.of()).size();
    }

    public int orange() {
        return colorDistribution.getOrDefault(Color.ORANGE, List.of()).size();
    }

    public int red() {
        return colorDistribution.getOrDefault(Color.RED, List.of()).size();
    }

    public int purple() {
        return colorDistribution.getOrDefault(Color.PURPLE, List.of()).size();
    }

}
