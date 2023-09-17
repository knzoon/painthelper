package org.knzoon.painthelper.model;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public interface TakeoverNeoView {

    Long getUserId();
    Long getZoneId();
    String getZoneName();
    Timestamp getTakeoverTime();
    Timestamp getLostTime();
    Integer getTp();
    Integer getPph();
    String getType();
    Double getLatitude();
    Double getLongitude();

    default ZonedDateTime getTakeoverTimeConverted() {
        if (getTakeoverTime() == null) {
            return null;
        }
        return getTakeoverTime().toInstant().atZone(ZoneId.systemDefault());
    }

    default ZonedDateTime getLostTimeConverted() {
        if (getLostTime() == null) {
            return null;
        }
        return getLostTime().toInstant().atZone(ZoneId.systemDefault());
    }

}
