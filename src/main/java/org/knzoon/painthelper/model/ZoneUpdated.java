package org.knzoon.painthelper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class ZoneUpdated {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long zoneId;

    private ZonedDateTime updatedTime;

    public ZoneUpdated() {
    }

    public ZoneUpdated(Long zoneId, ZonedDateTime updatedTime) {
        this.zoneId = zoneId;
        this.updatedTime = updatedTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public ZonedDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(ZonedDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
