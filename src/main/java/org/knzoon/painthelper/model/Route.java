package org.knzoon.painthelper.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private final List<Takeover> takeovers;

    public Route() {
        takeovers = new ArrayList<>();
    }

    public Route(Takeover takeover) {
        takeovers = new ArrayList<>();
        add(takeover);
    }

    public void add(Takeover takeover) {
        takeovers.add(takeover);
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
}
