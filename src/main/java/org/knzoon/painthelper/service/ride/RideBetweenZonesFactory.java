package org.knzoon.painthelper.service.ride;

import org.knzoon.painthelper.model.ride.RideBetweenZones;
import org.knzoon.painthelper.model.ride.TakeoverRideView;

import java.time.Duration;
import java.time.ZonedDateTime;

public class RideBetweenZonesFactory {

    private RideBetweenZonesFactory() {
    }

    public static RideBetweenZones create(TakeoverRideView fromTakeover, TakeoverRideView toTakeover, Integer roundId) {
        double distance = DistanceCalculator.kmBetweenPoints(fromTakeover.getLatitude(), fromTakeover.getLongitude(),
                toTakeover.getLatitude(), toTakeover.getLongitude());
        double duration = durationInHours(fromTakeover.getTakeoverTimeAsZonedDateTime(), toTakeover.getTakeoverTimeAsZonedDateTime());
        double kpmh = distance / duration;

        return new RideBetweenZones(
                fromTakeover.getZoneId(),
                toTakeover.getZoneId(),
                fromTakeover.getUserId(),
                kpmh,
                roundId);
    }

    static double durationInHours(ZonedDateTime fromTakeoverTime, ZonedDateTime toTakeoverTime) {
        Duration duration = Duration.between(fromTakeoverTime, toTakeoverTime);
        return duration.getSeconds() / 3600.0;
    }
}
