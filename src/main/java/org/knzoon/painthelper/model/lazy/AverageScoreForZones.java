package org.knzoon.painthelper.model.lazy;

import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.representation.lazy.LazyZoneRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AverageScoreForZones {
    private final Map<Long, List<Takeover>> takeoversPerZone;
    private final Map<Long, Zone> zonesMap;

    private Logger logger = LoggerFactory.getLogger(AverageScoreForZones.class);

    public AverageScoreForZones(List<Takeover> takeovers, Set<Zone> zones) {
        this.takeoversPerZone = takeovers.stream().collect(Collectors.groupingBy(Takeover::getZoneId));
        this.zonesMap = zones.stream().collect(Collectors.toMap(Zone::getId, Function.identity()));
    }

    public List<LazyZoneRepresentation> getZonesWithAverageScore(ZonedDateTime until) {
        return takeoversPerZone.values().stream()
                .map(takeovers -> toAverageScoreForZone(takeovers, until))
                .map(this::toLazyZone)
                .toList();
    }

    AverageScoreForZone toAverageScoreForZone(List<Takeover> takeovers, ZonedDateTime until) {
        Long zoneId = takeovers.get(0).getZoneId();
        Double averageScore = takeovers.stream().collect(Collectors.averagingDouble(takeover -> pointsFromTakeover(takeover, until)));
        return new AverageScoreForZone(zoneId, averageScore);
    }

    Double pointsFromTakeover(Takeover takeover, ZonedDateTime until) {
        PointsInDay pointsUntilNow = takeover.pointsUntilNow(until);
        Double effectivePoint = pointsUntilNow.getTotal();

        if (takeover.isNeutralZone()) {
            effectivePoint =  pointsUntilNow.getTotal() - 50;
        }

        logger.info("ZoneId: {}, total points: {}, after possible reduction: {}", takeover.getZoneId(), pointsUntilNow.getTotal(), effectivePoint);
        return effectivePoint;
    }

    LazyZoneRepresentation toLazyZone(AverageScoreForZone averageScoreForZone) {
        Zone zone = zonesMap.get(averageScoreForZone.zoneId());

        return new LazyZoneRepresentation(
                zone.getName(),
                zone.getLatitude(),
                zone.getLongitude(),
                averageScoreForZone.averageScore()
        );
    }
}
