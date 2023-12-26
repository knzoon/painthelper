package org.knzoon.painthelper.model;


import jakarta.persistence.Embeddable;

@Embeddable
public class ZoneInfo {

    private Long zoneId;

    private String name;

    private Double longitude;

    private Double latitude;

    private Long areaId;

    private String area;

    public ZoneInfo() {
    }

    public ZoneInfo(Long zoneId, String name, Double longitude, Double latitude, Long areaId, String area) {
        this.zoneId = zoneId;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.areaId = areaId;
        this.area = area;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getAreaId() {
        return areaId;
    }

    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
