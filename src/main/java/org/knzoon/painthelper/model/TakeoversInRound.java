package org.knzoon.painthelper.model;

import org.knzoon.painthelper.util.RoundCalculator;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

public class TakeoversInRound {
    private final Integer roundId;
    private final ZonedDateTime now;
    private final Integer currentDayOfRound;
    private final Map<Integer, List<Takeover>> takeoversPerDay;

    public TakeoversInRound(Integer roundId, ZonedDateTime now, List<Takeover> takeovers) {
        this.roundId = roundId;
        this.now = now;
        this.currentDayOfRound = RoundCalculator.dayOfRound(roundId, now);
        this.takeoversPerDay = takeovers.stream().collect(Collectors.groupingBy(Takeover::dayOfRound));
    }

    public PointsInRound calculatePointsPerDay(String username) {
        PointsInRound pointsInRound = new PointsInRound(username);

        for (int i = 1; i < currentDayOfRound + 1; i++) {
            pointsInRound.addDay(summPointsForDay(takeoversPerDay.get(i), now));
        }

        return pointsInRound;
    }

    private PointsInDay summPointsForDay(List<Takeover> takeovers, ZonedDateTime now) {
        if (takeovers == null) {
            return PointsInDay.ZERO;
        }

        return takeovers.stream().map(t -> t.pointsUntilNow(now)).reduce(PointsInDay.ZERO, PointsInDay::add);
    }

    public List<List<Route>> getRoutesPerDay() {
        List<List<Route>> routesPerDay = new ArrayList<>();

        for (int i = 1; i < currentDayOfRound + 1; i++) {
            routesPerDay.add(RouteFactory.from(takeoversPerDay.get(i)));
        }

        return routesPerDay;
    }

    public Set<Long> zonesInTakeovers() {
        return takeoversPerDay.values().stream()
                .flatMap(List::stream)
                .map(Takeover::getZoneId)
                .collect(Collectors.toSet());
    }
}
