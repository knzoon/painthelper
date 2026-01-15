package org.knzoon.painthelper.model;

public interface UniqueZoneView {
    String getZoneName();
    String getAreaName();
    Double getLatitude();
    Double getLongitude();
    Integer getTakes();

    default Color getColor() {
        if (getTakes() == 1) {
            return Color.GREEN;
        }

        if (getTakes() < 11) {
            return Color.YELLOW;
        }

        if (getTakes() < 21) {
            return Color.ORANGE;
        }

        if (getTakes() < 51) {
            return Color.RED;
        }

        return Color.PURPLE;
    }
}
