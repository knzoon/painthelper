package org.knzoon.painthelper.model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<Takeover> takeovers;
    private final ZonedDateTime now;
    private PointsInDay pointsInDayTotal;

    public Route(ZonedDateTime now) {
        takeovers = new ArrayList<>();
        this.now = now;
        this.pointsInDayTotal = PointsInDay.ZERO;
    }

    public Route(ZonedDateTime now, Takeover takeover) {
        this.now = now;
        takeovers = new ArrayList<>();
        takeovers.add(takeover);
        pointsInDayTotal = takeover.pointsUntilNow(now);
    }

    public void add(Takeover takeover) {
        takeovers.add(takeover);
        pointsInDayTotal = pointsInDayTotal.add(takeover.pointsUntilNow(now));
    }

    public boolean shouldContain(Takeover takeover) {
        if (isEmpty()) {
            return true;
        }

        Duration durationSinceLastTakeover = Duration.between(lastTakeover().getTakeoverTime(), takeover.getTakeoverTime());
        return durationSinceLastTakeover.toMinutes() < 20;
    }

    private Takeover lastTakeover() {
        return takeovers.get(takeovers.size() - 1);
    }

    public boolean isEmpty() {
        return takeovers.isEmpty();
    }

    public boolean hasMoreThanOneTake() {
        return takeovers.size() > 1;
    }

    public Integer nrofTakes() {
        return takeovers.size();
    }


    public Duration timeSpent() {
        if (takeovers.isEmpty()) {
            return Duration.ZERO;
        }

        return Duration.between(takeovers.get(0).getTakeoverTime(), lastTakeover().getTakeoverTime());
    }

    public List<Takeover> takeovers() {
        return takeovers;
    }

    public PointsInDay totalPoints() {
        return pointsInDayTotal;
    }
}
