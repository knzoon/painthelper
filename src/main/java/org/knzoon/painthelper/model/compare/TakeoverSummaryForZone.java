package org.knzoon.painthelper.model.compare;

import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.representation.compare.ZoneTakeoverRepresentation;
import org.knzoon.painthelper.representation.compare.ZoneTakeoverTotalRepresentation;
import org.knzoon.painthelper.service.TakeoverRepresentationConverter;
import org.knzoon.painthelper.util.DurationFormatter;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TakeoverSummaryForZone {
    private final List<Takeover> takeoversForZone;
    private final ZonedDateTime now;

    public TakeoverSummaryForZone(List<Takeover> takeoversForZone, ZonedDateTime now) {
        this.takeoversForZone = takeoversForZone;
        this.now = now;
    }

    public List<ZoneTakeoverRepresentation> getTakeovers() {
        return takeoversForZone.stream()
                .map(takeover -> TakeoverRepresentationConverter.toZoneTakeoverRepresentation(takeover, now))
                .toList();
   }

   public int getTp() {
        return takeoversForZone.isEmpty() ? 0 : takeoversForZone.getFirst().getTp();
   }

   public int getPph() {
       return takeoversForZone.isEmpty() ? 0 : takeoversForZone.getFirst().getPph();
   }

    public List<ZoneTakeoverTotalRepresentation> getTotalsPerUser() {
        return takeoversForZone.stream()
                .collect(Collectors.groupingBy(Takeover::getUser))
                .values().stream()
                .map(this::sumTakeovers)
                .sorted(Comparator.comparing(TakeoverTotalForZone::points, Comparator.reverseOrder())
                        .thenComparing(TakeoverTotalForZone::duration, Comparator.reverseOrder()))
                .map(this::toRepresentation)
                .toList();
    }

    TakeoverTotalForZone sumTakeovers(List<Takeover> takeoversForUser) {
        return takeoversForUser.stream()
                .map(this::toTotalForZone)
                .reduce(TakeoverTotalForZone.ZERO, TakeoverTotalForZone::add);

    }

    TakeoverTotalForZone toTotalForZone(Takeover takeover) {
        PointsInDay pointsUntilNow = takeover.pointsUntilNow(now);

        return new TakeoverTotalForZone(
                takeover.getUser(),
                1,
                pointsUntilNow.getTotalRounded(),
                pointsUntilNow.getDuration());
    }

    ZoneTakeoverTotalRepresentation toRepresentation(TakeoverTotalForZone total) {
        return new ZoneTakeoverTotalRepresentation(
                total.user().getUsername(),
                total.visits(),
                total.points(),
                DurationFormatter.format(total.duration()));
    }
}
