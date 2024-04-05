package org.knzoon.painthelper.representation.turfapi;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.knzoon.painthelper.model.ValidationException;
import org.knzoon.painthelper.model.Zone;

import java.time.ZonedDateTime;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ZoneFeedItemPart {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Region region;
    private UserMinimal currentOwner;
    private UserMinimal previousOwner;
    private ZoneType type;
    private Integer pointsPerHour;
    private Integer takeoverPoints;

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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public UserMinimal getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(UserMinimal currentOwner) {
        this.currentOwner = currentOwner;
    }

    public UserMinimal getPreviousOwner() {
        return previousOwner;
    }

    public void setPreviousOwner(UserMinimal previousOwner) {
        this.previousOwner = previousOwner;
    }

    public Long getRegionId() {
        return region.getId();
    }

    public String getRegionName() {
        return region.getName();
    }

    public Long getAreaId() {
        if (region.getArea() == null) {
            return null;
        }

        return region.getArea().getId();
    }

    public String getAreaName() {
        if (region.getArea() == null) {
            return null;
        }

        return region.getArea().getName();
    }

    public String getCountryCode() {
        return region.getCountry();
    }

    public ZoneType getType() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = type;
    }

    public Integer getPointsPerHour() {
        return pointsPerHour;
    }

    public void setPointsPerHour(Integer pointsPerHour) {
        this.pointsPerHour = pointsPerHour;
    }

    public Integer getTakeoverPoints() {
        return takeoverPoints;
    }

    public void setTakeoverPoints(Integer takeoverPoints) {
        this.takeoverPoints = takeoverPoints;
    }

    public boolean equalsExistingZone(Zone zoneFromDB) {
        if (zoneFromDB == null || !zoneFromDB.getId().equals(id)) {
            throw new ValidationException("Zone comparison done wrong");
        }

        return zoneNameEqual(zoneFromDB) && coordinatesEqual(zoneFromDB);
    }

    private boolean zoneNameEqual(Zone zoneFromDB) {
        return zoneFromDB.getName().equals(name);
    }

    private boolean coordinatesEqual(Zone zoneFromDB) {
        return zoneFromDB.getLatitude().equals(latitude) && zoneFromDB.getLongitude().equals(longitude);
    }

}
