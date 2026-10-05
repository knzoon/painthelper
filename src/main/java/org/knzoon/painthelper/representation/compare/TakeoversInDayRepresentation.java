package org.knzoon.painthelper.representation.compare;

import java.util.List;

public record TakeoversInDayRepresentation(
        List<RouteTotalRepresentation> routeTotals,
        List<RouteRepresentation> routes) {
}
