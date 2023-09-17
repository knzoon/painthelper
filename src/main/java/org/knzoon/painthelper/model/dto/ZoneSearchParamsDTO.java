package org.knzoon.painthelper.model.dto;

public class ZoneSearchParamsDTO {

    private final Long regionTakesId;
    private final Integer minTakes;
    private final Integer maxTakes;
    private final Long areaId;

    private final boolean searchForRound;

    public ZoneSearchParamsDTO(Long regionTakesId, Integer minTakes, Integer maxTakes, Long areaId, Integer searchForRound) {
        this.regionTakesId = regionTakesId;
        this.minTakes = minTakes;
        this.maxTakes = maxTakes;
        this.areaId = areaId;
        this.searchForRound = searchForRound == 1;
    }

    public Long getRegionTakesId() {
        return regionTakesId;
    }

    public Integer getMinTakes() {
        return minTakes;
    }

    public Integer getMaxTakes() {
        return maxTakes;
    }

    public Long getAreaId() {
        return areaId;
    }

    public boolean ignoreArea() {
        return areaId == 0;
    }

    public boolean isSearchForRound() {
        return searchForRound;
    }
}
