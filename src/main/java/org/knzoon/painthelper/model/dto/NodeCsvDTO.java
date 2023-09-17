package org.knzoon.painthelper.model.dto;

public class NodeCsvDTO {

    private final Long zoneId;
    private final String zoneName;
    private final Integer points;
    private final Double latitude;
    private final Double longitude;

    public NodeCsvDTO(Long zoneId, String zoneName, Integer points, Double latitude, Double longitude) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.points = points;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getZoneId() {
        return String.valueOf(zoneId);
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getPoints() {
        return String.valueOf(points);
    }

    public String getLatitude() {
        return String.valueOf(latitude);
    }

    public String getLongitude() {
        return String.valueOf(longitude);
    }
}
