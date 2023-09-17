package org.knzoon.painthelper.model.dto;

import org.knzoon.painthelper.model.RegionTakes;

public class RegionTakesDTO {
    private final RegionTakes regionTakes;

    private final String untaken;

    private final String green;

    private final String yellow;

    private final String orange;

    private final String red;

    private final String purple;

    public RegionTakesDTO(RegionTakes regionTakes, String untaken, String green, String yellow, String orange, String red, String purple) {
        this.regionTakes = regionTakes;
        this.untaken = untaken;
        this.green = green;
        this.yellow = yellow;
        this.orange = orange;
        this.red = red;
        this.purple = purple;
    }

    public RegionTakes getRegionTakes() {
        return regionTakes;
    }

    public String getUntaken() {
        return untaken;
    }

    public String getGreen() {
        return green;
    }

    public String getYellow() {
        return yellow;
    }

    public String getOrange() {
        return orange;
    }

    public String getRed() {
        return red;
    }

    public String getPurple() {
        return purple;
    }
}
