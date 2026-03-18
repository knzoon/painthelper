package org.knzoon.painthelper.model;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TakeoverTestbuilder {
    private Integer roundId = 150;
    private TakeoverType type = TakeoverType.TAKEOVER;
    private Long zoneId = 31342L;
    private String zonetype = null;
    private ZonedDateTime takeoverTime = ZonedDateTime.now();
    private User user = new User(666L, "Belsebub");
    private Integer pph = 9;
    private Integer tp = 65;
    private User previousUser = null;
    private User assistingUser = null;
    private ZonedDateTime lostTime = null;
    private User nextUser = null;


    private TakeoverTestbuilder() {
    }

    public static TakeoverTestbuilder builder() {
        return new TakeoverTestbuilder();
    }

    public TakeoverTestbuilder withPph(int pph) {
        this.pph = pph;
        return this;
    }

    public Takeover build() {
        Takeover takeover = new Takeover(roundId, type, zoneId, zonetype, takeoverTime, user, pph, tp, previousUser, assistingUser);
        if (lostTime != null) {
            takeover.setNextInfo(nextUser, lostTime);
        }
        return takeover;
    }
}
