package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.representation.LatestTakeoverInfoRepresentation;
import org.knzoon.painthelper.representation.compare.GraphDataRepresentation;
import org.knzoon.painthelper.representation.compare.TurfEffortRepresentation;
import org.knzoon.painthelper.service.TakeoverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TakeoverController {
    private final TakeoverService takeoverService;

    private Logger logger = LoggerFactory.getLogger(TakeoverController.class);

    @Autowired
    public TakeoverController(TakeoverService takeoverService) {
        this.takeoverService = takeoverService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/turfeffort")
    public TurfEffortRepresentation getTurfEffortForUser(@RequestParam(value = "username") String username) {
        return takeoverService.getTurfEffortForUserAndCurrentRound(username);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/graphs")
    public GraphDataRepresentation getGraphdata(@RequestParam(value = "username") String username) {
        return takeoverService.getGraphData(username);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/takeover/latest")
    public LatestTakeoverInfoRepresentation getLatestTakeover() {
        return takeoverService.getLatestTakeover();
    }

}
