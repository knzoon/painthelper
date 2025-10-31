package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.model.dto.ZoneSearchParamsDTO;
import org.knzoon.painthelper.representation.AreaRepresentation;
import org.knzoon.painthelper.representation.BroadcastMessageRepresentation;
import org.knzoon.painthelper.representation.RegionTakesRepresentation;
import org.knzoon.painthelper.representation.UniqueZoneRepresentation;
import org.knzoon.painthelper.representation.UniqueZoneSearchresultRepresentation;
import org.knzoon.painthelper.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ZoneController {

    private final ZoneService zoneService;

    private Logger logger = LoggerFactory.getLogger(ZoneController.class);

    @Autowired
    public ZoneController(ZoneService zoneService) {
        this.zoneService = zoneService;
    }

//    @CrossOrigin(origins = {"http://localhost:4200", "http://91.226.221.195:8080"})
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/searchzones")
    public UniqueZoneSearchresultRepresentation searchUniqueZones(@RequestParam(value = "regionTakesId") Long regionTakesId,
                                                                  @RequestParam(value = "minTakes") Integer minTakes,
                                                                  @RequestParam(value = "maxTakes") Integer maxTakes,
                                                                  @RequestParam(value = "areaId", defaultValue = "0") Long areaId,
                                                                  @RequestParam(value = "searchForRound", defaultValue = "0") Integer searchForRound) {
        ZoneSearchParamsDTO searchParamsDTO = new ZoneSearchParamsDTO(regionTakesId, minTakes, maxTakes, areaId, searchForRound);
        UniqueZoneSearchresultRepresentation searchresult = zoneService.searchUniqueZones(searchParamsDTO);
        return searchresult;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/regiontakes")
    public List<RegionTakesRepresentation> getRegionTakes(@RequestParam(value = "username") String username) {
        List<RegionTakesRepresentation> regionTakes = zoneService.getRegionTakes(username);

        if (regionTakes.isEmpty()) {
            logger.info("No RegionTakes found for Username: {}", username);
        } else {
            logger.info("User {} loaded with {} regions", username, regionTakes.size());
        }

        return regionTakes;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/areas")
    public List<AreaRepresentation> getDistinctAreasForRegionTakes(@RequestParam(value = "regionTakesId") Long regionTakesId) {
        List<AreaRepresentation> areas = zoneService.getDistinctAreasForRegionTakes(regionTakesId);

        if (areas.isEmpty()) {
            logger.info("No Areas found for RegionTakesId: {}", regionTakesId);
        }

        return areas;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/broadcast")
    public List<BroadcastMessageRepresentation> searchBroadcastMessages(@RequestParam(value = "userId") Long userId) {
        return zoneService.searchBroadcastMessages(userId);
    }

    @GetMapping("/api/testing")
    public List<UniqueZoneRepresentation> testing() {
        return zoneService.testing();
    }
}
