package org.knzoon.painthelper.representation;

public class AreaRepresentation {
    private final Long id;

    private final String areaName;

    private final TakesColorDistributionRepresentation takesColorDistribution;

    public AreaRepresentation(Long id, String areaName, TakesColorDistributionRepresentation takesColorDistribution) {
        this.id = id;
        this.areaName = areaName;
        this.takesColorDistribution = takesColorDistribution;
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
}
