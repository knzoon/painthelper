package org.knzoon.painthelper.model;

public class ZoneTestbuilder {
    private Long id = 31342L;
    private String name = "Testzonen";
    private Double latitude = 0.98765;
    private Double longitude = 1.23456;
    private Long regionId = 125L;
    private String regionName = "Västerbotten";
    private Long areaId = 42L;
    private String areaName = "Umeå kommun";
    private String countryCode = "se";

    private ZoneTestbuilder() {
    }

    public static ZoneTestbuilder builder() {
        return new ZoneTestbuilder();
    }

    public ZoneTestbuilder withId(Long zoneId) {
        this.id = zoneId;
        return this;
    }

    public ZoneTestbuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ZoneTestbuilder withLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public ZoneTestbuilder withLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Zone build() {
        return new Zone(id, name, latitude, longitude, regionId, regionName, areaId, areaName, countryCode);
    }
}
