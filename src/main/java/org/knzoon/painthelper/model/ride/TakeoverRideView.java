package org.knzoon.painthelper.model.ride;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface TakeoverRideView {
    Long getUserId();
    Long getZoneId();
    Timestamp getTakeoverTime();
    Double getLatitude();
    Double getLongitude();

    default ZonedDateTime getTakeoverTimeAsZonedDateTime() {
        return getTakeoverTime().toInstant().atZone(ZoneId.of("UTC"));
    }
}
