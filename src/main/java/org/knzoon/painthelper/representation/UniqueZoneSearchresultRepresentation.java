package org.knzoon.painthelper.representation;

import java.util.List;

public class UniqueZoneSearchresultRepresentation {
    private final List<UniqueZoneRepresentation> zones;
    private final Double latitude;
    private final Double longitude;

    public UniqueZoneSearchresultRepresentation(List<UniqueZoneRepresentation> zones, Double latitude, Double longitude) {
        this.zones = zones;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public List<UniqueZoneRepresentation> getZones() {
        return zones;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
