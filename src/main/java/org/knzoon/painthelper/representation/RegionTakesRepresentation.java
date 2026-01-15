package org.knzoon.painthelper.representation;

public class RegionTakesRepresentation {
    private final Long id;
    private final String regionName;
    private final Long regionId;
    private final Long userId;

    private final TakesColorDistributionRepresentation takesColorDistribution;
    private final TakesColorDistributionRepresentation roundColorDistribution;

    public RegionTakesRepresentation(Long id,
                                     String regionName,
                                     Long regionId,
                                     Long userId,
                                     TakesColorDistributionRepresentation takesColorDistribution,
                                     TakesColorDistributionRepresentation roundColorDistribution) {
        this.id = id;
        this.regionName = regionName;
        this.regionId = regionId;
        this.userId = userId;
        this.takesColorDistribution = takesColorDistribution;
        this.roundColorDistribution = roundColorDistribution;
    }

    public Long getId() {
        return id;
    }

    public String getRegionName() {
        return regionName;
    }

    public Long getRegionId() {
        return regionId;
    }

    public Long getUserId() {
        return userId;
    }

    public TakesColorDistributionRepresentation getTakesColorDistribution() {
        return takesColorDistribution;
    }

    public TakesColorDistributionRepresentation getRoundColorDistribution() {
        return roundColorDistribution;
    }
}
