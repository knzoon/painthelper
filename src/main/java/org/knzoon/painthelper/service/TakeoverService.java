package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.model.PointsInRound;
import org.knzoon.painthelper.model.Route;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverRepository;
import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.model.UserRepository;
import org.knzoon.painthelper.representation.LatestTakeoverInfoRepresentation;
import org.knzoon.painthelper.representation.compare.DailyGraphDatasetRepresentation;
import org.knzoon.painthelper.representation.compare.GraphDataRepresentation;
import org.knzoon.painthelper.representation.compare.GraphDatapointRepresentation;
import org.knzoon.painthelper.representation.compare.GraphDatasetRepresentation;
import org.knzoon.painthelper.representation.compare.TakeoverSummaryDayRepresentation;
import org.knzoon.painthelper.representation.compare.TurfEffortRepresentation;
import org.knzoon.painthelper.util.RoundCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TakeoverService {
    private final TakeoverRepository takeoverRepository;
    private final UserRepository userRepository;

    @Autowired
    public TakeoverService(TakeoverRepository takeoverRepository, UserRepository userRepository) {
        this.takeoverRepository = takeoverRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LatestTakeoverInfoRepresentation getLatestTakeover() {
        Takeover latestTakeover = takeoverRepository.findLatestTakeover();
        return new LatestTakeoverInfoRepresentation(latestTakeover.getZoneId(), latestTakeover.getTakeoverTime());
    }

    @Transactional
    public TurfEffortRepresentation getTurfEffortForUserAndCurrentRound(String username) {
        User user = userRepository.findByUsername(username);
        Integer roundId = RoundCalculator.roundFromDateTime(ZonedDateTime.now());
        List<Takeover> takeovers = takeoverRepository.findAllByRoundIdAndUserOrderById(roundId, user);
        Route currentRoute = new Route();
        List<Route> routes = new ArrayList<>();

        for (Takeover takeover : takeovers) {
            if (currentRoute.shouldContain(takeover)) {
                currentRoute.add(takeover);
            } else {
                routes.add(currentRoute);
                currentRoute = new Route(takeover);
            }
        }

        if (!currentRoute.isEmpty()) {
            routes.add(currentRoute);
        }

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        List<Route> filteredRoutes = routes.stream().filter(Route::hasMoreThanOneTake).collect(Collectors.toList());

        List<PointsInDay> pointsPerDay = takeovers.stream().map(t -> t.pointsUntilNow(now)).collect(Collectors.toList());

        return new TurfEffortRepresentation(
                username,
                calculateTimeSpentInRoutes(filteredRoutes),
                calculatePointsForTakeovers(pointsPerDay),
                takeovers.size(),
                filteredRoutes.size(),
                getTakesInRoutes(filteredRoutes),
                calculatePphForTakeovers(pointsPerDay));
    }

    private String calculateTimeSpentInRoutes(List<Route> routes) {
        Duration totalDuration = routes.stream().map(Route::timeSpent).reduce(Duration.ZERO, Duration::plus);
        return totalDuration.toHours() + "h " + totalDuration.toMinutesPart() + "m";
    }

    private Integer calculatePointsForTakeovers(List<PointsInDay> pointsPerDay) {
        Double points = pointsPerDay.stream().map(PointsInDay::getTotal).collect(Collectors.summingDouble(Double::doubleValue));
        return (int) Math.round(points);
    }

    private Integer calculatePphForTakeovers(List<PointsInDay> pointsPerDay) {
        Double points = pointsPerDay.stream().map(PointsInDay::getPph).collect(Collectors.summingDouble(Double::doubleValue));
        return (int) Math.round(points);
    }

    private Integer getTakesInRoutes(List<Route> routes) {
        return routes.stream().map(Route::nrofTakes).reduce(0, Integer::sum);
    }

    @Transactional
    public GraphDataRepresentation getGraphData(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return GraphDataRepresentation.tom();
        }

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        PointsInRound pointsInRound = generateListOfPointsPerDayThisFarInCurrentRound(user, now);

        GraphDatasetRepresentation graphdataCumulative = getGraphdataCumulative(pointsInRound);
        List<DailyGraphDatasetRepresentation> graphdataDaily = getGraphdataDaily(pointsInRound);
        List<TakeoverSummaryDayRepresentation> takeoverSummaryDaily = getTakeoverSummaryDaily(pointsInRound);

        return new GraphDataRepresentation(graphdataCumulative, graphdataDaily, takeoverSummaryDaily);
    }

    private PointsInRound generateListOfPointsPerDayThisFarInCurrentRound(User user, ZonedDateTime now) {
        Integer roundId = RoundCalculator.roundFromDateTime(now);
        Integer currentDayOfRound = RoundCalculator.dayOfRound(roundId, now);
        List<Takeover> takeovers = takeoverRepository.findAllByRoundIdAndUserOrderById(roundId, user);

        return calculatePointsPerDay(takeovers, now, currentDayOfRound, user.getUsername());
    }

    private PointsInRound calculatePointsPerDay(List<Takeover> takeovers, ZonedDateTime now, int numberOfDaysInRoundYet, String username) {
        PointsInRound pointsInRound = new PointsInRound(username);

        Map<Integer, List<Takeover>> takeoversPerDay = takeovers.stream().collect(Collectors.groupingBy(t -> RoundCalculator.dayOfRound(t.getRoundId(), t.getTakeoverTime())));

        for (int i = 1; i < numberOfDaysInRoundYet + 1; i++) {
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

    private GraphDatasetRepresentation getGraphdataCumulative(PointsInRound pointsInRound) {
        List<Integer> cumulativePointsPerDay = pointsInRound.calculatePointsCumulative();

        return new GraphDatasetRepresentation(
                pointsInRound.getUsername(),
                toDatapointRepresentationList(cumulativePointsPerDay),
                pointsInRound.getPointsTotalSum());
    }

    private List<GraphDatapointRepresentation> toDatapointRepresentationList(List<Integer> pointsPerDay) {
        List<GraphDatapointRepresentation> representationList = new ArrayList<>();
        int day = 1;

        for (Integer points : pointsPerDay) {
            representationList.add(new GraphDatapointRepresentation(Integer.valueOf(day++).toString(), points));
        }

        return representationList;
    }

    private List<DailyGraphDatasetRepresentation> getGraphdataDaily(PointsInRound pointsPerDay) {
        String username = pointsPerDay.getUsername();
        Integer pointsTotalSum = pointsPerDay.getPointsTotalSum();

        var takepointDataset = DailyGraphDatasetRepresentation.takepointDataset(
                username,
                pointsPerDay.dailyTakeoverPoints(),
                pointsTotalSum);

        var pphDataset = DailyGraphDatasetRepresentation.pphDataset(
                username,
                pointsPerDay.dailyPphPoints(),
                pointsTotalSum);

        return List.of(takepointDataset, pphDataset);
    }

    private List<TakeoverSummaryDayRepresentation> getTakeoverSummaryDaily(PointsInRound pointsInRound) {
        List<TakeoverSummaryDayRepresentation> representations = new ArrayList<>();
        List<PointsInDay> pointsInDay = pointsInRound.getPointsInDay();

        for (int i = 0; i < pointsInDay.size(); i++) {
            representations.add(TakeoverSummaryDayRepresentationConverter.toRepresentation(i, pointsInDay.get(i)));
        }

        return representations;
    }

}
