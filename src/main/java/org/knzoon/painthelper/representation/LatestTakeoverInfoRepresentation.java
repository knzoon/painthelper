package org.knzoon.painthelper.representation;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record LatestTakeoverInfoRepresentation (
    Long zoneId,
    LocalDateTime takeoverTime) {

    public LatestTakeoverInfoRepresentation(Long zoneId, ZonedDateTime takeoverTime) {
        this(zoneId, takeoverTime.toLocalDateTime());
    }
}
