package org.knzoon.painthelper.representation.turfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneMinimal {
    private Long id;
    private String name;
    private Region region;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Long getAreaId() {
        if (region.getArea() == null ) {
            return null;
        }

        return region.getArea().getId();
    }

    public String getArea() {
        if (region.getArea() == null ) {
            return null;
        }

        return region.getArea().getName();
    }
}
