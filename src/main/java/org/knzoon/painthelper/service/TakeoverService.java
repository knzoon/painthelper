package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.*;
import org.knzoon.painthelper.model.lazy.AverageScoreForZones;
import org.knzoon.painthelper.representation.LatestTakeoverInfoRepresentation;
import org.knzoon.painthelper.representation.compare.*;
import org.knzoon.painthelper.representation.lazy.LazyZoneRepresentation;
import org.knzoon.painthelper.util.DurationFormatter;
import org.knzoon.painthelper.util.RoundCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TakeoverService {
    private final TakeoverRepository takeoverRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private  final TakeoverRepresentationConverter takeoverRepresentationConverter;

    @Autowired
    public TakeoverService(TakeoverRepository takeoverRepository,
                           UserRepository userRepository,
                           ZoneRepository zoneRepository,
                           TakeoverRepresentationConverter takeoverRepresentationConverter) {
        this.takeoverRepository = takeoverRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.takeoverRepresentationConverter = takeoverRepresentationConverter;
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

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        List<Route> filteredRoutes = RouteFactory.from(takeovers)
                .stream()
                .filter(Route::hasMoreThanOneTake)
                .collect(Collectors.toList());

        List<PointsInDay> pointsPerDay = takeovers.stream().map(t -> t.pointsUntilNow(now)).collect(Collectors.toList());

        return new TurfEffortRepresentation(
                username,
                calculateTimeSpentInRoutes(filteredRoutes),
                calculatePointsForTakeovers(pointsPerDay),
                takeovers.size(),
                filteredRoutes.size(),
                getTakesInRoutes(filteredRoutes),
                calculatePphForTakeovers(pointsPerDay),
                PphDistributionFactory.createForUniqueZones(takeovers),
                PphDistributionFactory.createForAllTakeovers(takeovers));
    }

    private String calculateTimeSpentInRoutes(List<Route> routes) {
        Duration totalDuration = routes.stream().map(Route::timeSpent).reduce(Duration.ZERO, Duration::plus);
        return DurationFormatter.format(totalDuration);
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
        List<Takeover> takeovers = takeoverRepository.findAllByRoundIdAndUserOrderById(roundId, user);
        TakeoversInRound takeoversInRound = new TakeoversInRound(roundId, now, takeovers);

        return takeoversInRound.calculatePointsPerDay(user.getUsername());
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

    @Transactional
    public List<List<List<TakeoverRepresentation>>> getTakeoversForUser(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return List.of();
        }

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        Integer roundId = RoundCalculator.roundFromDateTime(now);
        List<Takeover> takeovers = takeoverRepository.findAllByRoundIdAndUserOrderById(roundId, user);
        TakeoversInRound takeoversInRound = new TakeoversInRound(roundId, now, takeovers);

        Map<Long, Zone> zoneMap = zoneRepository.findByIdIn(takeoversInRound.zonesInTakeovers()).stream()
                .collect(Collectors.toMap(Zone::getId, Function.identity()));

        List<List<Route>> routesPerDayInRound = takeoversInRound.getRoutesPerDay();

        return routesPerDayInRound.stream()
                .map(routesInDay -> takeoverRepresentationConverter.toRepresentation(routesInDay, now, zoneMap))
                .toList();
    }


    @Transactional
    public List<LazyZoneRepresentation> getZonesWithScoreAverage() {
        List<Takeover> takeovers = List.of();
        Set<Zone> zones = Set.of();
        AverageScoreForZones averageScoreForZones = new AverageScoreForZones(takeovers, zones);
        return averageScoreForZones.getZonesWithAverageScore(ZonedDateTime.now());
    }

    @Transactional
    public ZoneTakeoverSummaryRepresentation getZoneTakeoverSummary(Long zoneId) {
        Optional<Zone> foundZone = zoneRepository.findById(zoneId);

        if (foundZone.isEmpty()) {
            return null;
        }

        Zone zone = foundZone.get();

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        Integer roundId = RoundCalculator.roundFromDateTime(now);

        List<Takeover> takeovers = takeoverRepository.findforRoundAndZone(roundId, zoneId);

        List<ZoneTakeoverRepresentation> takeoverRepresentations = takeovers.stream()
                .map(takeover -> takeoverRepresentationConverter.toZoneTakeoverRepresentation(takeover, now))
                .toList();

        int tp = takeovers.isEmpty() ? 0 : takeovers.getFirst().getTp();
        int pph = takeovers.isEmpty() ? 0 : takeovers.getFirst().getPph();

        return new ZoneTakeoverSummaryRepresentation(zone.getName(), zone.getAreaName(), tp, pph, takeoverRepresentations);
    }

    @Transactional
    public ToplistUniqueZonesInAreaRepresentation getToplistUniqueZonesInArea(Long areaId) {
        List<AreaView> areas = zoneRepository.findAreaById(areaId);

        if (areas.isEmpty()) {
            return new ToplistUniqueZonesInAreaRepresentation("Zone is missing", 0, List.of());
        }

        Integer nrofZones = Math.toIntExact(zoneRepository.countByAreaId(areaId));
        String areaName = areas.getFirst().getArea();

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        Integer roundId = RoundCalculator.roundFromDateTime(now);
        List<NumberOfUniqueZonesForUserRepresentation> users = takeoverRepository.findForRoundAndArea(roundId, areaId).stream()
                .map(u -> tillRepresentation(u))
                .toList();

        return new ToplistUniqueZonesInAreaRepresentation(areaName, nrofZones, users);
    }

    private NumberOfUniqueZonesForUserRepresentation tillRepresentation(UserUniqueZonesView user) {
        return new NumberOfUniqueZonesForUserRepresentation(user.getUsername(), user.getNrof());
    }
}
