package org.knzoon.painthelper.model.ride;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class RideBetweenZones {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long fromZoneId;
    private Long toZoneId;
    private Long userId;
    private Double kmph;
    private Integer roundId;

    public RideBetweenZones() {
    }

    public RideBetweenZones(Long fromZoneId, Long toZoneId, Long userId, Double kmph, Integer roundId) {
        this.fromZoneId = fromZoneId;
        this.toZoneId = toZoneId;
        this.userId = userId;
        this.kmph = kmph;
        this.roundId = roundId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromZoneId() {
        return fromZoneId;
    }

    public void setFromZoneId(Long fromZoneId) {
        this.fromZoneId = fromZoneId;
    }

    public Long getToZoneId() {
        return toZoneId;
    }

    public void setToZoneId(Long toZoneId) {
        this.toZoneId = toZoneId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getKmph() {
        return kmph;
    }

    public void setKmph(Double kmph) {
        this.kmph = kmph;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }
}
