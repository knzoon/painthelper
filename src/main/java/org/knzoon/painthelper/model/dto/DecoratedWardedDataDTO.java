package org.knzoon.painthelper.model.dto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class DecoratedWardedDataDTO {
    private final Long userId;
    private final String username;
    private final Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion;

    public DecoratedWardedDataDTO(Long userId, String username, Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion) {
        this.userId = userId;
        this.username = username;
        this.uniqueZonesPerRegion = uniqueZonesPerRegion;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Map<Long, List<DecoratedWardedZoneDTO>> getUniqueZonesPerRegion() {
        return uniqueZonesPerRegion;
    }

    public Long getRegionId() {
        return 0L;
    }

    public String getRegionName() {
        return "";
    }

    public List<DecoratedWardedZoneDTO> getUniqueZones() {
        return Collections.emptyList();
    }
}
