package org.knzoon.painthelper.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PointsInRound {
    private final String username;
    private final List<PointsInDay> points;

    public PointsInRound(String username) {
        this.username = username;
        points = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void addDay(PointsInDay pointsInDay) {
        points.add(pointsInDay);
    }

    public List<PointsInDay> getPointsInDay() {
        return points;
    }

    public List<Integer> calculatePointsCumulative() {
        List<Integer> cumulativePoints = new ArrayList<>();

        Double partialSum = 0.0;

        for (PointsInDay pointsInDay : points) {
            partialSum += pointsInDay.getTotal();
            cumulativePoints.add((int) Math.round(partialSum));
        }

//        pointsPerDay.forEach(d -> logger.info("per day {}", d));
//        cumulativePoints.forEach(i -> logger.info("per day rounded {}", i));

        return cumulativePoints;
    }

    public List<Integer> dailyTakeoverPoints() {
        return points.stream().map(PointsInDay::getTakepoint).map(this::toInteger).collect(Collectors.toList());
    }

    public List<Integer> dailyPphPoints() {
        return points.stream().map(PointsInDay::getPph).map(this::toInteger).collect(Collectors.toList());
    }

    private Integer toInteger(Double points) {
        return (int) Math.round(points);
    }

    public Integer getPointsTotalSum() {
        if (points.isEmpty()) {
            return 0;
        }

        return calculatePointsCumulative().get(points.size() - 1);
    }
}
