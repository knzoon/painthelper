package org.knzoon.painthelper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class ErrorZone {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private String zoneName;

    private Integer takes;

    private Double longitude;

    private Double latitude;

    private ZonedDateTime created;


    public ErrorZone() {
    }

    public ErrorZone(Long userId, String zoneName, Integer takes, Double longitude, Double latitude, ZonedDateTime created) {
        this.userId = userId;
        this.zoneName = zoneName;
        this.takes = takes;
        this.longitude = longitude;
        this.latitude = latitude;
        this.created = created;
    }

    public String getZoneName() {
        return zoneName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public Integer getTakes() {
        return takes;
    }

    public void setTakes(Integer takes) {
        this.takes = takes;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
