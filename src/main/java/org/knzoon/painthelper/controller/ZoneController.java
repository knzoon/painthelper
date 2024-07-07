package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.model.dto.ZoneSearchParamsDTO;
import org.knzoon.painthelper.representation.*;
import org.knzoon.painthelper.representation.compare.GraphDatasetRepresentation;
import org.knzoon.painthelper.representation.compare.TurfEffortRepresentation;
import org.knzoon.painthelper.representation.turfapi.IdParameter;
import org.knzoon.painthelper.representation.turfapi.UserInfoFromTurfApi;
import org.knzoon.painthelper.service.UserService;
import org.knzoon.painthelper.service.ZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class ZoneController {

    private final ZoneService zoneService;
    private final UserService userService;

    private Logger logger = LoggerFactory.getLogger(ZoneController.class);

    @Autowired
    public ZoneController(ZoneService zoneService, UserService userService) {
        this.zoneService = zoneService;
        this.userService = userService;
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
    @GetMapping("/api/users")
    public List<UserRepresentation> searchUsers(@RequestParam(value = "searchString") String searchString) {
        return zoneService.searchUsers(searchString);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/allusers")
    public List<UserRepresentation> searchAllUsers(@RequestParam(value = "searchString") String searchString) {
        return zoneService.searchAllUsers(searchString);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/broadcast")
    public List<BroadcastMessageRepresentation> searchBroadcastMessages(@RequestParam(value = "userId") Long userId) {
        return zoneService.searchBroadcastMessages(userId);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/turfeffort")
    public TurfEffortRepresentation getTurfEffortForUser(@RequestParam(value = "username") String username) {
        return zoneService.getTurfEffortForUserAndCurrentRound(username);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/cumulative")
    public GraphDatasetRepresentation getGraphdataCumulative(@RequestParam(value = "username") String username) {
        return zoneService.getGraphData(username).getCumulative();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/api/userinfo")
    public List<UserInfoFromTurfApi> getUserInfo(@RequestBody List<IdParameter> users) {
        return userService.getUserInfo(users);
    }

    @GetMapping("/api/testing")
    public List<UniqueZoneRepresentation> testing() {
        return zoneService.testing();
    }
}
