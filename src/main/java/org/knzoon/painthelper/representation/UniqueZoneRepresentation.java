package org.knzoon.painthelper.representation;

public record UniqueZoneRepresentation(
        String zoneName,
        String areaName,
        Double latitude,
        Double longitude,
        Integer takes) {
}
