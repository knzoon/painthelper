package org.knzoon.painthelper.model;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private final List<Takeover> takeovers;

    public Route() {
        takeovers = List.of();
    }

    public Route(Takeover takeover) {
        takeovers = List.of(takeover);
    }

    private Route(List<Takeover> takeovers) {
        this.takeovers = Collections.unmodifiableList(takeovers);
    }

    /**
     * Route is unmutable so add() returns a new Route
     * */
    public Route add(Takeover takeover) {
        List<Takeover> takeoversWithAddedTakeover = new ArrayList<>();
        takeoversWithAddedTakeover.addAll(takeovers);
        takeoversWithAddedTakeover.add(takeover);
        return new Route(takeoversWithAddedTakeover);
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
}
