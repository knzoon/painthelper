package org.knzoon.painthelper.service.ride;

import org.knzoon.painthelper.model.ride.RideBetweenZones;
import org.knzoon.painthelper.model.ride.RideBetweenZonesRepository;
import org.knzoon.painthelper.model.ride.TakeoverRideView;
import org.knzoon.painthelper.representation.ride.CreateRidesResult;
import org.knzoon.painthelper.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class RideService {

    private final RideBetweenZonesRepository rideBetweenZonesRepository;

    private Logger logger = LoggerFactory.getLogger(RideService.class);

    @Autowired
    public RideService(RideBetweenZonesRepository rideBetweenZonesRepository) {
        this.rideBetweenZonesRepository = rideBetweenZonesRepository;
    }

    @Transactional
    public CreateRidesResult createRidesForRoundAndArea(int roundId, Long userId, Long areaId) {
        logger.info("Starting creation of rides");
        List<TakeoverRideView> takeovers = rideBetweenZonesRepository.findTakeoversByRoundUserArea(roundId, userId, areaId);
        List<RideBetweenZones> ridesBetweenZones = extractRidesBetweenZones(takeovers, roundId);
        rideBetweenZonesRepository.saveAll(ridesBetweenZones);

        logger.info("Created {}", ridesBetweenZones.size());
        return new CreateRidesResult(ridesBetweenZones.size());
    }

    private List<RideBetweenZones> extractRidesBetweenZones(List<TakeoverRideView> takeovers, Integer roundId) {
        List<RideBetweenZones> rides = new ArrayList<>();

        if (takeovers.isEmpty()) {
            return rides;
        }

        Iterator<TakeoverRideView> takeoverIterator = takeovers.iterator();
        TakeoverRideView fromTakeover = takeoverIterator.next();

        while (takeoverIterator.hasNext()) {
            TakeoverRideView toTakeover = takeoverIterator.next();
            if (notTakenTooFarApart(fromTakeover.getTakeoverTimeAsZonedDateTime(), toTakeover.getTakeoverTimeAsZonedDateTime())) {
                rides.add(RideBetweenZonesFactory.create(fromTakeover, toTakeover, roundId));
            }
            fromTakeover = toTakeover;
        }

        return rides;
    }

    private boolean notTakenTooFarApart(ZonedDateTime fromTakeoverTime, ZonedDateTime toTakeoverTime) {
        Duration durationBetweenTakeovers = Duration.between(fromTakeoverTime, toTakeoverTime);
        return durationBetweenTakeovers.toMinutes() < 25;

    }
}
