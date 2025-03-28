package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.representation.ImportResultRepresentation;
import org.knzoon.painthelper.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ManageCatalogdataController {
    private final FeedService feedService;

    @Autowired
    public ManageCatalogdataController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/api/importAllZones")
    public ImportResultRepresentation importAllZones() {
        int nrofImported = feedService.importAllZonesFromWarded();
        return new ImportResultRepresentation("Import av Zoner", nrofImported);
    }

}
