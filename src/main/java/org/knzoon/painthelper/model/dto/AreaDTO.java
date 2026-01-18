package org.knzoon.painthelper.model.dto;

import org.knzoon.painthelper.model.AreaView;
import org.knzoon.painthelper.model.TakesColorDistribution;

public class AreaDTO {
    private final AreaView areaView;
    private final TakesColorDistribution takesColorDistribution;
    private final TakesColorDistribution roundColorDistribution;

    public AreaDTO(AreaView areaView, TakesColorDistribution takesColorDistribution, TakesColorDistribution roundColorDistribution) {
        this.areaView = areaView;
        this.takesColorDistribution = takesColorDistribution;
        this.roundColorDistribution = roundColorDistribution;
    }

    public AreaView getAreaView() {
        return areaView;
    }

    public TakesColorDistribution getTakesColorDistribution() {
        return takesColorDistribution;
    }

    public TakesColorDistribution getRoundColorDistribution() {
        return roundColorDistribution;
    }
}
