package org.knzoon.painthelper.representation;

public record AreaRepresentation(
        Long id,
        String areaName,
        TakesColorDistributionRepresentation takesColorDistribution,
        TakesColorDistributionRepresentation roundColorDistribution) {
}
