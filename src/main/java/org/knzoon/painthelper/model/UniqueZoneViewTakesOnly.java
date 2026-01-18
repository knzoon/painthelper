package org.knzoon.painthelper.model;

public record UniqueZoneViewTakesOnly(int takes) implements UniqueZoneView {
    @Override
    public String getZoneName() {
        return null;
    }

    @Override
    public String getAreaName() {
        return null;
    }

    @Override
    public Double getLatitude() {
        return null;
    }

    @Override
    public Double getLongitude() {
        return null;
    }

    @Override
    public Integer getTakes() {
        return takes;
    }
}
