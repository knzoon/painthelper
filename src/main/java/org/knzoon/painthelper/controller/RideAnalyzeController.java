package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.representation.compare.TakeoverRepresentation;
import org.knzoon.painthelper.representation.ride.CreateRidesResult;
import org.knzoon.painthelper.service.ride.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RideAnalyzeController {
    private final RideService rideService;

    @Autowired
    public RideAnalyzeController(RideService rideService) {
        this.rideService = rideService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/createrides/roundId/{roundId}/userId/{userId}/areaId/{areaId}")
    public CreateRidesResult createRidesForRoundUserArea(
            @PathVariable(value = "roundId") Integer roundId,
            @PathVariable(value = "userId") Long userId,
            @PathVariable(value = "areaId") Long areaId) {
        // TODO Should be POST and not taking userId
        return rideService.createRidesForRoundAndArea(roundId, userId, areaId);
    }

}
