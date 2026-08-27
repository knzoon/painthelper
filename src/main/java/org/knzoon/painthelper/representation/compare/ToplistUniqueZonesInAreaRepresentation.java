package org.knzoon.painthelper.representation.compare;

import java.util.List;

public record ToplistUniqueZonesInAreaRepresentation(
        String areaName,
        Integer nrofZones,
        List<NumberOfUniqueZonesForUserRepresentation> toplist) {
}
