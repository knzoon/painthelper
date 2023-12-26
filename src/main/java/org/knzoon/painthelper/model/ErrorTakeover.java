package org.knzoon.painthelper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class ErrorTakeover {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long zoneId;

    private ZonedDateTime takeoverTime;

    private ZonedDateTime created;

    public ErrorTakeover() {
    }

    public ErrorTakeover(Long userId, Long zoneId, ZonedDateTime takeoverTime, ZonedDateTime created) {
        this.userId = userId;
        this.zoneId = zoneId;
        this.takeoverTime = takeoverTime;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public ZonedDateTime getTakeoverTime() {
        return takeoverTime;
    }

    public void setTakeoverTime(ZonedDateTime takeoverTime) {
        this.takeoverTime = takeoverTime;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
}
