package org.knzoon.painthelper.representation.lazy;

public record LazyZoneRepresentation(
        String zoneName,
        Double latitude,
        Double longitude,
        Double averageScore) {
}
