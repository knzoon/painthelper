package org.knzoon.painthelper.representation;

public class AreaRepresentation {
    private final Long id;
    private final String areaName;

    private final TakesColorDistributionRepresentation takesColorDistribution;
    private final TakesColorDistributionRepresentation roundColorDistribution;

    public AreaRepresentation(Long id,
                              String areaName,
                              TakesColorDistributionRepresentation takesColorDistribution,
                              TakesColorDistributionRepresentation roundColorDistribution) {
        this.id = id;
        this.areaName = areaName;
        this.takesColorDistribution = takesColorDistribution;
        this.roundColorDistribution = roundColorDistribution;
    }

    public Long getId() {
        return id;
    }

    public String getAreaName() {
        return areaName;
    }

    public TakesColorDistributionRepresentation getTakesColorDistribution() {
        return takesColorDistribution;
    }

    public TakesColorDistributionRepresentation getRoundColorDistribution() {
        return roundColorDistribution;
    }
}
