package org.knzoon.painthelper.representation;

public class RegionTakesRepresentation {
    private final Long id;

    private final String regionName;

    private final Long regionId;

    private final Long userId;

    private final String untaken;

    private final String green;

    private final String yellow;

    private final String orange;

    private final String red;

    private final String purple;

    public RegionTakesRepresentation(Long id, String regionName, Long regionId, Long userId, String untaken, String green, String yellow, String orange, String red, String purple) {
        this.id = id;
        this.regionName = regionName;
        this.regionId = regionId;
        this.userId = userId;
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

    public String getRegionName() {
        return regionName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public Long getUserId() {
        return userId;
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
