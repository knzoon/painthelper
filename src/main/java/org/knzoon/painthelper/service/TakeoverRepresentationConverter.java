package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.model.Route;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.representation.compare.TakeoverRepresentation;
import org.knzoon.painthelper.util.DurationFormatter;
import org.knzoon.painthelper.util.UTCSwedishTimeConverter;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
        builder.withTakeoverTime(UTCSwedishTimeConverter.convert(takeover.getTakeoverTime()).format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .withTp(takeover.getTp())
                .withPph(takeover.getPph())
                .withActivity(takeover.activity())
                .withPoints(pointsUntilNow.getTotalRounded())
                .withDuration(DurationFormatter.format(pointsUntilNow.getDuration()))
                .withAccumulating(pointsUntilNow.hasAccumulatingPph());
        zone.ifPresent(z -> builder.withZoneName(z.getName()));
        zone.ifPresent(z -> Optional.ofNullable(z.getAreaName()).ifPresent(builder::withAreaName));
        takeover.previousUser().ifPresent(user -> builder.withPreviousUser(user.getUsername()));
        takeover.nextUser().ifPresent(user -> builder.withNextUser(user.getUsername()));
        takeover.assistingUser().ifPresent(user -> builder.withAssistingUser(user.getUsername()));

        return builder.build();
    }

}
