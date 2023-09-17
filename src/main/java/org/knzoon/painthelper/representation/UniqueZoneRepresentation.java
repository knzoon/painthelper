package org.knzoon.painthelper.representation;

public class UniqueZoneRepresentation {
    private final String zoneName;
    private final String areaName;
    private final Double latitude;
    private final Double longitude;
    private final Integer takes;

    public UniqueZoneRepresentation(String zoneName, String areaName, Double latitude, Double longitude, Integer takes) {
        this.zoneName = zoneName;
        this.areaName = areaName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.takes = takes;
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getAreaName() {
        return areaName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getTakes() {
        return takes;
    }
}
