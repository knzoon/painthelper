package org.knzoon.painthelper.representation.compare;

import java.util.List;

public record ZoneTakeoverSummaryRepresentation(
    String zoneName,
    String areaName,
    Integer tp,
    Integer pph,
    List<ZoneTakeoverRepresentation> takeovers) {
}
