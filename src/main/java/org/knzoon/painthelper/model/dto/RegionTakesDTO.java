package org.knzoon.painthelper.model.dto;

import org.knzoon.painthelper.model.RegionTakes;
import org.knzoon.painthelper.representation.TakesColorDistributionRepresentation;

public class RegionTakesDTO {
    private final RegionTakes regionTakes;
    private final TakesColorDistributionRepresentation takesColorDistributionRepresentation;

    public RegionTakesDTO(RegionTakes regionTakes, TakesColorDistributionRepresentation takesColorDistributionRepresentation) {
        this.regionTakes = regionTakes;
        this.takesColorDistributionRepresentation = takesColorDistributionRepresentation;
    }

    public RegionTakes getRegionTakes() {
        return regionTakes;
    }

    public TakesColorDistributionRepresentation getTakesColorDistributionRepresentation() {
        return takesColorDistributionRepresentation;
    }
}
