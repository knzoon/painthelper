package org.knzoon.painthelper.representation;

public record RegionTakesRepresentation(
        Long id,
        String regionName,
        Long regionId,
        Long userId,
        TakesColorDistributionRepresentation takesColorDistribution,
        TakesColorDistributionRepresentation roundColorDistribution) {
}
