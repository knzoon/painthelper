package org.knzoon.painthelper.model.dto;

import org.knzoon.painthelper.model.RegionTakes;
import org.knzoon.painthelper.representation.TakesColorDistributionRepresentation;

public class RegionTakesDTO {
    private final RegionTakes regionTakes;
    private final TakesColorDistributionRepresentation takesColorDistributionRepresentation;
    private final TakesColorDistributionRepresentation roundColorDistributionRepresentation;

    public RegionTakesDTO(RegionTakes regionTakes,
                          TakesColorDistributionRepresentation takesColorDistributionRepresentation,
                          TakesColorDistributionRepresentation roundColorDistributionRepresentation) {
        this.regionTakes = regionTakes;
        this.takesColorDistributionRepresentation = takesColorDistributionRepresentation;
        this.roundColorDistributionRepresentation = roundColorDistributionRepresentation;
    }

    public RegionTakes getRegionTakes() {
        return regionTakes;
    }

    public TakesColorDistributionRepresentation getTakesColorDistributionRepresentation() {
        return takesColorDistributionRepresentation;
    }

    public TakesColorDistributionRepresentation getRoundColorDistributionRepresentation() {
        return roundColorDistributionRepresentation;
    }
}
