package org.knzoon.painthelper.representation.compare;

public record TurfEffortRepresentation(
        String username,
        String timeSpent,
        Integer points,
        Integer takes,
        Integer routes,
        Integer takesInRoutes,
        Integer pointsByPph,
        PphDistributionRepresentation pphDistribution,
        PphDistributionRepresentation pphDistributionAllTakeovers) {
}
