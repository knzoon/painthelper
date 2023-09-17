package org.knzoon.painthelper.representation;

public class AreaRepresentation {
    private final Long id;

    private final String areaName;

    private final String untaken;

    private final String green;

    private final String yellow;

    private final String orange;

    private final String red;

    private final String purple;


    public AreaRepresentation(Long id, String areaName, String untaken, String green, String yellow, String orange, String red, String purple) {
        this.id = id;
        this.areaName = areaName;
        this.untaken = untaken;
        this.green = green;
        this.yellow = yellow;
        this.orange = orange;
        this.red = red;
        this.purple = purple;
    }

    public Long getId() {
        return id;
    }

    public String getAreaName() {
        return areaName;
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
