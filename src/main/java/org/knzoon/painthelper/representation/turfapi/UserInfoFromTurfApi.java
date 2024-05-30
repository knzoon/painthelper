package org.knzoon.painthelper.representation.turfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoFromTurfApi {
    private String country;
    private Long id;
    private String name;
    private Integer points;
    private Integer pointsPerHour;
    private RegionMinimal region;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

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

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getPointsPerHour() {
        return pointsPerHour;
    }

    public void setPointsPerHour(Integer pointsPerHour) {
        this.pointsPerHour = pointsPerHour;
    }

    public RegionMinimal getRegion() {
        return region;
    }

    public void setRegion(RegionMinimal region) {
        this.region = region;
    }
}
