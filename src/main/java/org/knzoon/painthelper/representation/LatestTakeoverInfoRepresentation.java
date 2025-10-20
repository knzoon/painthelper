package org.knzoon.painthelper.representation;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public class LatestTakeoverInfoRepresentation {
    private final Long zoneId;
    private final LocalDateTime takeoverTime;

    public LatestTakeoverInfoRepresentation(Long zoneId, ZonedDateTime takeoverTime) {
        this.zoneId = zoneId;
        this.takeoverTime = takeoverTime.toLocalDateTime();
    }

    public Long getZoneId() {
        return zoneId;
    }

    public LocalDateTime getTakeoverTime() {
        return takeoverTime;
    }
}
