package org.knzoon.painthelper.model.dto;

import org.knzoon.painthelper.model.ZoneInfo;

public class DecoratedWardedZoneDTO {

    private final ZoneInfo zone;

    private final String regionName;

    private final Integer takes;

    public DecoratedWardedZoneDTO(ZoneInfo zone, String regionName, Integer takes) {
        this.zone = zone;
        this.regionName = regionName;
        this.takes = takes;
    }

    public ZoneInfo getZone() {
        return zone;
    }

    public Integer getTakes() {
        return takes;
    }

    public String getRegionName() {
        return regionName;
    }
}
