package org.knzoon.painthelper.representation;

import java.util.List;

public record UniqueZoneSearchresultRepresentation(
        List<UniqueZoneRepresentation> zones,
        Double latitude,
        Double longitude) {
}
