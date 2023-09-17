package org.knzoon.painthelper.model.dto;

import org.knzoon.painthelper.representation.warded.UniqueWardedZones;

import java.util.Collections;
import java.util.List;

public class WardedDataDTO {

    private final UniqueWardedZones uniqueWardedZones;

    private final String username;

    public WardedDataDTO(UniqueWardedZones uniqueWardedZones, String username) {
        this.uniqueWardedZones = uniqueWardedZones;
        this.username = username;
    }

    public UniqueWardedZones getUniqueWardedZones() {
        return uniqueWardedZones;
    }

    public String getUsername() {
        return username;
    }

}
