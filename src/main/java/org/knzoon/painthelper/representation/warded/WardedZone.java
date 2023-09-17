package org.knzoon.painthelper.representation.warded;

public class WardedZone {

    private String type;
    private Geometry geometry;
    private ZoneProperties properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ZoneProperties getProperties() {
        return properties;
    }

    public void setProperties(ZoneProperties properties) {
        this.properties = properties;
    }

    public String zoneName() {
        return properties.getTitle();
    }

    public Double getLongitude() {
        return geometry.getCoordinates().get(0);
    }

    public Double getLatitude() {
        return geometry.getCoordinates().get(1);
    }

    public Integer getTakes() {
        return properties.getCount();
    }
}
