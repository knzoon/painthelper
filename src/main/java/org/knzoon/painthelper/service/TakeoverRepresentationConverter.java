package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.model.Route;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.representation.compare.TakeoverRepresentation;
import org.knzoon.painthelper.representation.compare.ZoneTakeoverRepresentation;
import org.knzoon.painthelper.util.DurationFormatter;
import org.knzoon.painthelper.util.UTCSwedishTimeConverter;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TakeoverRepresentationConverter {

    public List<List<TakeoverRepresentation>> toRepresentation(List<Route> routesInDay, ZonedDateTime now, Map<Long, Zone> zoneMap) {
        return routesInDay.stream()
                .map(route -> toRepresentation(route, now, zoneMap))
                .toList();
    }

    List<TakeoverRepresentation> toRepresentation(Route route, ZonedDateTime now, Map<Long, Zone> zoneMap) {
        return route.takeovers().stream()
                .map(takeover -> toRepresentation(takeover, now, zoneMap))
                .toList();
    }

    TakeoverRepresentation toRepresentation(Takeover takeover, ZonedDateTime now, Map<Long, Zone> zoneMap) {
        PointsInDay pointsUntilNow = takeover.pointsUntilNow(now);
        Optional<Zone> zone = Optional.ofNullable(zoneMap.get(takeover.getZoneId()));
        var builder = TakeoverRepresentation.builder();
        builder.withTakeoverTime(takeovetimeConverter(takeover.getTakeoverTime()))
                .withTp(takeover.getTp())
                .withPph(takeover.getPph())
                .withActivity(takeover.activity())
                .withPoints(pointsUntilNow.getTotalRounded())
                .withDuration(DurationFormatter.format(pointsUntilNow.getDuration()))
                .withAccumulating(pointsUntilNow.hasAccumulatingPph());
        zone.ifPresent(z -> builder.withZoneName(z.getName()));
        zone.ifPresent(z -> builder.withZoneId(z.getId()));
        zone.ifPresent(z -> Optional.ofNullable(z.getAreaName()).ifPresent(builder::withAreaName));
        zone.ifPresent(z -> Optional.ofNullable(z.getAreaId()).ifPresent(builder::withAreaId));
        takeover.previousUser().ifPresent(user -> builder.withPreviousUser(user.getUsername()));
        takeover.nextUser().ifPresent(user -> builder.withNextUser(user.getUsername()));
        takeover.assistingUser().ifPresent(user -> builder.withAssistingUser(user.getUsername()));

        return builder.build();
    }

    private String takeovetimeConverter(ZonedDateTime takeovertime) {
        return UTCSwedishTimeConverter.convert(takeovertime).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    private String takeovetimeWithDateConverter(ZonedDateTime takeovertime) {
        return UTCSwedishTimeConverter.convert(takeovertime).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    ZoneTakeoverRepresentation toZoneTakeoverRepresentation(Takeover takeover, ZonedDateTime now) {
        PointsInDay pointsUntilNow = takeover.pointsUntilNow(now);

        return new ZoneTakeoverRepresentation(
                takeovetimeWithDateConverter(takeover.getTakeoverTime()),
                takeover.getUser().getUsername(),
                pointsUntilNow.getTotalRounded(),
                DurationFormatter.format(pointsUntilNow.getDuration()));
    }
}
