package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.representation.ride.CreateRidesResult;
import org.knzoon.painthelper.service.ride.ManageRidedataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RideAnalyzeController {
    private final ManageRidedataService manageRidedataService;

    @Autowired
    public RideAnalyzeController(ManageRidedataService manageRidedataService) {
        this.manageRidedataService = manageRidedataService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/createrides/roundId/{roundId}/areaId/{areaId}")
    public CreateRidesResult createRidesForRoundUserArea(
            @PathVariable(value = "roundId") Integer roundId,
            @PathVariable(value = "areaId") Long areaId) {
        // TODO Should be POST and not taking userId
        return manageRidedataService.createRidesForRoundAndArea(roundId, areaId);
    }

}
