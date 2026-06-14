package org.knzoon.painthelper.service.ride;

import org.knzoon.painthelper.model.UserIdView;
import org.knzoon.painthelper.representation.ride.CreateRidesResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class ManageRidedataService {

    private final RideService rideService;

    private Logger logger = LoggerFactory.getLogger(ManageRidedataService.class);

    @Autowired
    public ManageRidedataService(RideService rideService) {
        this.rideService = rideService;
    }

    public CreateRidesResult createRidesForRoundAndArea(int roundId, Long areaId) {
        Instant started = Instant.now();

        List<UserIdView> users = rideService.getDistinctUsersForRoundAndArea(roundId, areaId);
        int ridesCreated = users.stream()
                .map(user -> rideService.createRidesForRoundUserAndArea(roundId, user.getUserId(), areaId))
                .map(CreateRidesResult::numberOfRidesCreated)
                .mapToInt(Integer::intValue)
                .sum();

        Instant finished = Instant.now();
        logger.info("Created {} rides in {}", ridesCreated, Duration.between(started, finished));
        return new CreateRidesResult(ridesCreated);
    }
}
