package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.*;
import org.knzoon.painthelper.model.dto.DecoratedWardedDataDTO;
import org.knzoon.painthelper.model.dto.DecoratedWardedZoneDTO;
import org.knzoon.painthelper.model.dto.ImportResultWardedDTO;
import org.knzoon.painthelper.model.dto.RegionTakesDTO;
import org.knzoon.painthelper.model.dto.WardedDataDTO;
import org.knzoon.painthelper.model.dto.ZoneSearchParamsDTO;
import org.knzoon.painthelper.representation.*;
import org.knzoon.painthelper.representation.compare.GraphDatapointRepresentation;
import org.knzoon.painthelper.representation.compare.GraphDatasetRepresentation;
import org.knzoon.painthelper.representation.compare.TurfEffortRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class ZoneService {

    private final WardedZoneDecorator wardedZoneDecorator;
    private final RegionTakesRepository regionTakesRepository;
    private final UniqueZoneRepository uniqueZoneRepository;
    private final UserRepository userRepository;
    private final ZoneRepository zoneRepository;
    private final BroadcastMessageRepository broadcastMessageRepository;
    private final TakeoverRepository takeoverRepository;
    private final RoundCalculator roundCalculator;
    private final PanToCalculator panToCalculator;

    private Logger logger = LoggerFactory.getLogger(ZoneService.class);

    @Autowired
    public ZoneService(WardedZoneDecorator wardedZoneDecorator, RegionTakesRepository regionTakesRepository,
                       UniqueZoneRepository uniqueZoneRepository, UserRepository userRepository,
                       ZoneRepository zoneRepository, BroadcastMessageRepository broadcastMessageRepository,
                       TakeoverRepository takeoverRepository, RoundCalculator roundCalculator, PanToCalculator panToCalculator) {
        this.wardedZoneDecorator = wardedZoneDecorator;
        this.regionTakesRepository = regionTakesRepository;
        this.uniqueZoneRepository = uniqueZoneRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.broadcastMessageRepository = broadcastMessageRepository;
        this.takeoverRepository = takeoverRepository;
        this.roundCalculator = roundCalculator;
        this.panToCalculator = panToCalculator;
    }

    @Transactional
    public List<ImportResultWardedDTO> importZones(WardedDataDTO wardedDataDTO) throws URISyntaxException {

        DecoratedWardedDataDTO decoratedWardedData = wardedZoneDecorator.decorate(wardedDataDTO);


        Long userId = decoratedWardedData.getUserId();
        String username = decoratedWardedData.getUsername();
        User currentUser;

        Optional<User> userInDB = userRepository.findById(userId);

        if (userInDB.isPresent()) {
            currentUser = userInDB.get();
        } else {
            currentUser = new User(userId, username);
            currentUser = userRepository.save(currentUser);
            logger.info("Skapar användare: {}", currentUser.getUsername());
        }


        Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion = decoratedWardedData.getUniqueZonesPerRegion();

        List<ImportResultWardedDTO> importResults = uniqueZonesPerRegion.entrySet().stream().map(region -> importRegion(region, userId)).collect(Collectors.toList());
        currentUser.setImported(true);
        currentUser.setLastImport(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));

        return importResults;
    }

    private ImportResultWardedDTO importRegion(Map.Entry<Long, List<DecoratedWardedZoneDTO>> regionMapEntry, Long userId) {
        Long regionId = regionMapEntry.getKey();
        List<DecoratedWardedZoneDTO> zones = regionMapEntry.getValue();

        if (zones.isEmpty()) {
            logger.info("Försöker importera region med id {} utan tagna zoner", regionId);
            throw new ValidationException("Försöker importera region utan tagna zoner");
        }

        String regionName = zones.get(0).getRegionName();

        boolean regionTakesExist = regionTakesRepository.existsByUserIdAndRegionId(userId, regionId);

        if (!regionTakesExist) {
            RegionTakes newRegionTakes = new RegionTakes(regionName, regionId, userId);
            regionTakesRepository.save(newRegionTakes);
            logger.info("Skapar en regionsdatahållare för userId {} och region {}", userId, regionName);
        }

        RegionTakes regionTakes = regionTakesRepository.findByUserIdAndRegionId(userId, regionId);

        List<Long> zoneIds = regionTakes.getUniqueZones().stream().map(UniqueZone::getId).collect(Collectors.toList());
        uniqueZoneRepository.deleteAllByIdInBatch(zoneIds);

        List<UniqueZone> newUniqueZones = zones.stream().map(z -> createZoneFromWardedData(z, regionTakes)).collect(Collectors.toList());
        uniqueZoneRepository.saveAll(newUniqueZones);

        return new ImportResultWardedDTO(regionTakesExist, regionName, newUniqueZones.size());
    }


    private UniqueZone createZoneFromWardedData(DecoratedWardedZoneDTO zoneDTO, RegionTakes regionTakes) {
        return new UniqueZone(zoneDTO.getTakes(), zoneDTO.getZone(), regionTakes);
    }

    @Transactional
    public List<UniqueZoneRepresentation> testing() {
        List<Zone> zones = zoneRepository.findZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(127L, 79L, 2064L);
        return zones.stream().map(this::toRepresentation).collect(Collectors.toList());
    }

    @Transactional
    public UniqueZoneSearchresultRepresentation searchUniqueZones(ZoneSearchParamsDTO searchParamsDTO) {
        List<UniqueZoneRepresentation> zonesWithTakes;

        if (searchParamsDTO.isSearchForRound()) {
            zonesWithTakes = searchUniqueZonesInCurrentRound(searchParamsDTO);
        } else {
            zonesWithTakes = searchUniqueZonesTotally(searchParamsDTO);
        }

        Point panTo = panToCalculator.panTo(zonesWithTakes);

        return new UniqueZoneSearchresultRepresentation(zonesWithTakes, panTo.latitude(), panTo.longitude());
    }

    private List<UniqueZoneRepresentation> searchUniqueZonesInCurrentRound(ZoneSearchParamsDTO searchParamsDTO) {
        Integer roundId = roundCalculator.roundFromDateTime(ZonedDateTime.now());
        logger.info("Search round: {} min: {} max: {}", roundId, searchParamsDTO.getMinTakes(), searchParamsDTO.getMaxTakes());
        List<UniqueZoneView> searchResult;
        // TODO verifiera att regionTakes hittas
        RegionTakes regionTakes = regionTakesRepository.findById(searchParamsDTO.getRegionTakesId()).get();
        Long userId = regionTakes.getUserId();
        Long regionId = regionTakes.getRegionId();

        if (searchParamsDTO.ignoreArea()) {
            searchResult = takeoverRepository.findUniqueZonesForUserRoundAndRegion(roundId, userId, regionId);
        } else {
            searchResult = takeoverRepository.findUniqueZonesForUserRoundAreaAndRegion(roundId, userId, regionId, searchParamsDTO.getAreaId());
        }

        List<UniqueZoneRepresentation> zonesWithTakes = searchResult.stream().filter(z -> filterByTakes(z, searchParamsDTO)).map(this::toRepresentation).collect(Collectors.toList());

        if (searchParamsDTO.getMinTakes() == 0) {
            zonesWithTakes.addAll(zonesWithoutTakesInRound(searchParamsDTO, searchResult, regionTakes));
        }

        return zonesWithTakes;
    }

    private boolean filterByTakes(UniqueZoneView zone, ZoneSearchParamsDTO searchParamsDTO) {
        return zone.getTakes().compareTo(searchParamsDTO.getMinTakes()) > -1 && zone.getTakes().compareTo(searchParamsDTO.getMaxTakes()) < 1;
    }

    private List<UniqueZoneRepresentation> searchUniqueZonesTotally(ZoneSearchParamsDTO searchParamsDTO) {
        logger.info("Search usual way min: {} max: {}", searchParamsDTO.getMinTakes(), searchParamsDTO.getMaxTakes());
        List<UniqueZone> searchResult;
        if (searchParamsDTO.ignoreArea()) {
            searchResult = uniqueZoneRepository.findByRegionTakesIdAndTakesBetweenOrderByTakesDescZoneAreaAscZoneNameAsc(searchParamsDTO.getRegionTakesId(), searchParamsDTO.getMinTakes(), searchParamsDTO.getMaxTakes());
        } else {
            searchResult = uniqueZoneRepository.findByRegionTakesIdAndZoneAreaIdAndTakesBetweenOrderByTakesDescZoneNameAsc(searchParamsDTO.getRegionTakesId(), searchParamsDTO.getAreaId(), searchParamsDTO.getMinTakes(), searchParamsDTO.getMaxTakes());
        }

        List<UniqueZoneRepresentation> zonesWithTakes = searchResult.stream().map(this::toRepresentation).collect(Collectors.toList());

        if (searchParamsDTO.getMinTakes() == 0) {
            zonesWithTakes.addAll(zonesWithoutTakes(searchParamsDTO));
        }

        return zonesWithTakes;
    }

    private List<UniqueZoneRepresentation> zonesWithoutTakesInRound(ZoneSearchParamsDTO searchParamsDTO, List<UniqueZoneView> zonesWithTakes, RegionTakes regionTakes) {
        Map<String, Integer> takenZones = zonesWithTakes.stream().collect(Collectors.toMap(UniqueZoneView::getZoneName, UniqueZoneView::getTakes));
        List<Zone> searchResult;

        if (searchParamsDTO.ignoreArea()) {
            searchResult = zoneRepository.findByRegionId(regionTakes.getRegionId());
        } else {
            searchResult = zoneRepository.findByRegionIdAndAreaId(regionTakes.getRegionId(), searchParamsDTO.getAreaId());
        }

        return searchResult.stream().filter(z -> !takenZones.containsKey(z.getName())).map(this::toRepresentation).collect(Collectors.toList());
    }

    private List<UniqueZoneRepresentation> zonesWithoutTakes(ZoneSearchParamsDTO searchParamsDTO) {
        RegionTakes regionTakes = regionTakesRepository.findById(searchParamsDTO.getRegionTakesId()).get();
        List<Zone> searchResult;

        if (searchParamsDTO.ignoreArea()) {
             searchResult = zoneRepository.findZonesByNotTakenAndRegionIdAndRegionTakesId(regionTakes.getRegionId(), searchParamsDTO.getRegionTakesId());
        } else {
            searchResult = zoneRepository.findZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(regionTakes.getRegionId(), searchParamsDTO.getRegionTakesId(), searchParamsDTO.getAreaId());
        }

        return searchResult.stream().map(this::toRepresentation).collect(Collectors.toList());
    }

    private UniqueZoneRepresentation toRepresentation(UniqueZoneView uniqueZone) {
        return new UniqueZoneRepresentation(uniqueZone.getZoneName(), uniqueZone.getAreaName(), uniqueZone.getLatitude(), uniqueZone.getLongitude(), uniqueZone.getTakes());
    }

    private UniqueZoneRepresentation toRepresentation(UniqueZone uniqueZone) {
        ZoneInfo zone = uniqueZone.getZone();
        return new UniqueZoneRepresentation(zone.getName(), zone.getArea(),  zone.getLatitude(), zone.getLongitude(), uniqueZone.getTakes());
    }

    private UniqueZoneRepresentation toRepresentation(Zone zone) {
        return new UniqueZoneRepresentation(zone.getName(), zone.getAreaName(), zone.getLatitude(), zone.getLongitude(), 0);
    }


    @Transactional
    public List<RegionTakesRepresentation> getRegionTakes(String username) {
        User user = getUser(username);

        if (user == null) {
            return Collections.emptyList();
        }

//        Instant before = Instant.now();
        Iterable<RegionTakes> regionTakes = regionTakesRepository.findAllByUserIdOrderByRegionName(user.getId());
//        Instant afterRegionTakesFetched = Instant.now();
        List<RegionTakesDTO> regionTakesDTOS = getListOfRegionsDecoratedWithDetails(regionTakes);
//        Instant afterDecoration = Instant.now();
//        logger.info("Time spent total {} Time spent decoration {}", Duration.between(before, afterDecoration).toMillis(), Duration.between(afterRegionTakesFetched, afterDecoration).toMillis());

        return regionTakesDTOS.stream().map(this::toRepresentation).collect(Collectors.toList());
    }

    private User getUser(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        User user = userRepository.findByUsername(username);
        return user;
    }

    private List<RegionTakesDTO> getListOfRegionsDecoratedWithDetails(Iterable<RegionTakes> regionTakes) {
        return StreamSupport.stream(regionTakes.spliterator(), false).map(this::decorateRegionTakes).collect(Collectors.toList());
    }

    private RegionTakesDTO decorateRegionTakes(RegionTakes regionTakes) {

//        Instant before = Instant.now();
        String untaken = zoneRepository.countZonesByNotTakenAndRegionIdAndRegionTakesId(regionTakes.getRegionId(), regionTakes.getId()).toString();
//        Instant afterUntaken = Instant.now();
        String green = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakes.getId(), 1, 1).toString();
//        Instant afterGreen = Instant.now();
        String yellow = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakes.getId(), 2, 10).toString();
//        Instant afterYellow = Instant.now();
        String orange = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakes.getId(), 11, 20).toString();
//        Instant afterOrange = Instant.now();
        String red = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakes.getId(), 21, 50).toString();
//        Instant afterRed = Instant.now();
        String purple = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakes.getId(), 51, 100000).toString();
//        Instant afterPurple = Instant.now();
//        logger.info("Region {}", regionTakes.getRegionName());
//        logger.info("Time total {} Time untaken {} Time green {} Time Yellow {} Time Orange {} Time Red {} Time Purple {}",
//                Duration.between(before, afterPurple).toMillis(),
//                Duration.between(before, afterUntaken).toMillis(),
//                Duration.between(afterUntaken, afterGreen).toMillis(),
//                Duration.between(afterGreen, afterYellow).toMillis(),
//                Duration.between(afterYellow, afterOrange).toMillis(),
//                Duration.between(afterOrange, afterRed).toMillis(),
//                Duration.between(afterRed, afterPurple).toMillis()
//                );
        return new RegionTakesDTO(regionTakes, untaken, green, yellow, orange, red, purple);
    }

    private RegionTakesRepresentation toRepresentation(RegionTakesDTO regionTakesDTO) {
        RegionTakes regionTakes = regionTakesDTO.getRegionTakes();
        return new RegionTakesRepresentation(regionTakes.getId(), regionTakes.getRegionName(), regionTakes.getRegionId(), regionTakes.getUserId(),
                regionTakesDTO.getUntaken(), regionTakesDTO.getGreen(), regionTakesDTO.getYellow(), regionTakesDTO.getOrange(), regionTakesDTO.getRed(), regionTakesDTO.getPurple());
    }

    @Transactional
    public List<AreaRepresentation> getDistinctAreasForRegionTakes(Long regionTakesId) {
        List<AreaView> areasWithTakenZones = uniqueZoneRepository.findDistinctAreasByTakenAndRegionTakesId(regionTakesId);
        Map<Long, AreaView> mappedTakenAreas = areasWithTakenZones.stream().collect(Collectors.toMap(AreaView::getAreaId, Function.identity()));

        RegionTakes regionTakes = regionTakesRepository.findById(regionTakesId).get();
        List<AreaView> distinctAreasByRegionId = zoneRepository.findDistinctAreasByRegionId(regionTakes.getRegionId());

        List<AreaView> areasWithoutTakenZones = distinctAreasByRegionId.stream().filter(areaView -> hasNoTakenZones(areaView, mappedTakenAreas)).collect(Collectors.toList());

        List<AreaRepresentation> completeAreaList = new ArrayList<>();

        if (!areasWithTakenZones.isEmpty()) {
            completeAreaList.addAll(areasWithTakenZones.stream().map(areaView -> toRepresentation(areaView, regionTakesId, regionTakes.getRegionId())).collect(Collectors.toList()));
        }

        completeAreaList.addAll(areasWithoutTakenZones.stream().map(areaView -> toRepresentation(areaView, regionTakesId, regionTakes.getRegionId())).collect(Collectors.toList()));

        return completeAreaList;
    }

    private boolean hasNoTakenZones(AreaView areaView, Map<Long, AreaView> mapOfTaken) {
        return !mapOfTaken.containsKey(areaView.getAreaId());
    }

    private AreaRepresentation toRepresentation(AreaView areaView, Long regionTakesId, Long regionId) {
        String areaString = areaView.getArea() + " (" + areaView.getAntal() + ")";
        String untaken = zoneRepository.countZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(regionId, regionTakesId, areaView.getAreaId()).toString();

        if (areaView.getAntal() < 1 ) {
            return new AreaRepresentation(areaView.getAreaId(), areaString, untaken, "0", "0", "0", "0", "0");
        } else {
            String green = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaView.getAreaId(), 1, 1).toString();
            String yellow = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaView.getAreaId(), 2, 10).toString();
            String orange = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaView.getAreaId(), 11, 20).toString();
            String red = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaView.getAreaId(), 21, 50).toString();
            String purple = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaView.getAreaId(), 51, 100000).toString();
            return new AreaRepresentation(areaView.getAreaId(), areaString, untaken, green, yellow, orange, red, purple);
        }
    }

    @Transactional
    public List<UserRepresentation> searchUsers(String searchString) {
        if (searchString.isEmpty()) {
            return Collections.emptyList();
        }
        List<User> users = userRepository.findAllByUsernameIsStartingWithAndImportedIsTrueOrderByUsername(searchString);
        return users.stream().map(this::toRepresentation).collect(Collectors.toList());

    }

    @Transactional
    public List<UserRepresentation> searchAllUsers(String searchString) {
        if (searchString.isEmpty()) {
            return Collections.emptyList();
        }

        List<User> users = userRepository.findAllByUsernameIsStartingWithOrderByUsername(searchString);
        return users.stream().map(this::toRepresentation).collect(Collectors.toList());
    }


    @Transactional
    public List<BroadcastMessageRepresentation> searchBroadcastMessages(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        Optional<User> userFromDB = userRepository.findById(userId);

        if (userFromDB.isPresent()) {

            List<BroadcastMessage> messagesFound = broadcastMessageRepository.findAllByImportNeededAfterIsAfterOrderByImportNeededAfter(getLastImport(userFromDB.get()));

            if (!messagesFound.isEmpty()) {
                return messagesFound.stream().map(this::toRepresentation).collect(Collectors.toList());
            }
        }

        return Collections.emptyList();
    }

    private BroadcastMessageRepresentation toRepresentation(BroadcastMessage broadcastMessage) {
        return new BroadcastMessageRepresentation(broadcastMessage.getMessage(), broadcastMessage.getImportNeededAfter());
    }

    private ZonedDateTime getLastImport(User user) {
        if (user.getLastImport() == null) {
            return ZonedDateTime.of(LocalDateTime.of(2022, 1, 1, 0, 0), ZoneId.of("UTC"));
        }

        return user.getLastImport();
    }

    private UserRepresentation toRepresentation(User user) {
        return new UserRepresentation(user.getId(), user.getUsername());
    }

    @Transactional
    public TurfEffortRepresentation getTurfEffortForUserAndCurrentRound(String username) {
        User user = userRepository.findByUsername(username);
        Integer roundId = roundCalculator.roundFromDateTime(ZonedDateTime.now());
        List<Takeover> takeovers = takeoverRepository.findAllByRoundIdAndUserOrderById(roundId, user);
        Route currentRoute = new Route();
        List<Route> routes = new ArrayList<>();

        for (Takeover takeover : takeovers) {
            if (currentRoute.shouldContain(takeover)) {
                currentRoute.add(takeover);
            } else {
                routes.add(currentRoute);
                currentRoute = new Route(takeover);
            }
        }

        if (!currentRoute.isEmpty()) {
            routes.add(currentRoute);
        }

        List<Route> filteredRoutes = routes.stream().filter(Route::hasMoreThanOneTake).collect(Collectors.toList());

        return new TurfEffortRepresentation(username, calculateTimeSpentInRoutes(filteredRoutes), calculatePointsForTakeovers(takeovers), takeovers.size(), filteredRoutes.size(), getTakesInRoutes(filteredRoutes));
    }

    private String calculateTimeSpentInRoutes(List<Route> routes) {
        Duration totalDuration = routes.stream().map(Route::timeSpent).reduce(Duration.ZERO, Duration::plus);
        return totalDuration.toHours() + "h " + totalDuration.toMinutesPart() + "m";
    }

    private Integer calculatePointsForTakeovers(List<Takeover> takeovers) {
        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        Double points = takeovers.stream().map(t -> t.pointsUntilNow(now)).collect(Collectors.summingDouble(Double::doubleValue));
        return (int) Math.round(points);
    }

    private Integer getTakesInRoutes(List<Route> routes) {
        return routes.stream().map(Route::nrofTakes).reduce(0, Integer::sum);
    }

    @Transactional
    public GraphDatasetRepresentation getGraphdataCumulative(String username) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return new GraphDatasetRepresentation("unknown", List.of());
        }

        ZonedDateTime now = Instant.now().atZone(ZoneId.of("UTC"));
        Integer roundId = roundCalculator.roundFromDateTime(now);
        Integer currentDayOfRound = roundCalculator.dayOfRound(roundId, now);
        List<Takeover> takeovers = takeoverRepository.findAllByRoundIdAndUserOrderById(roundId, user);

        List<Integer> pointsPerDay = calculatePointsCumulative(takeovers, now, currentDayOfRound);

        return new GraphDatasetRepresentation(username, toDatapointRepresentationList(pointsPerDay));
    }

    private List<GraphDatapointRepresentation> toDatapointRepresentationList(List<Integer> pointsPerDay) {
        List<GraphDatapointRepresentation> representationList = new ArrayList<>();
        int day = 1;

        for (Integer points : pointsPerDay) {
            representationList.add(new GraphDatapointRepresentation(Integer.valueOf(day++).toString(), points));
        }

        return representationList;
    }

    private List<Integer> calculatePointsCumulative(List<Takeover> takeovers, ZonedDateTime now, int numberOfDaysInRoundYet) {
        List<Double> pointsPerDay = calculatePointsPerDay(takeovers, now, numberOfDaysInRoundYet);
        List<Integer> cumulativePoints = new ArrayList<>();

        Double partialSum = 0.0;

        for (Double dayPoints : pointsPerDay) {
            partialSum += dayPoints;
            cumulativePoints.add((int) Math.round(partialSum));
        }

//        pointsPerDay.forEach(d -> logger.info("per day {}", d));
//        cumulativePoints.forEach(i -> logger.info("per day rounded {}", i));

        return cumulativePoints;
    }

    private List<Double> calculatePointsPerDay(List<Takeover> takeovers, ZonedDateTime now, int numberOfDaysInRoundYet) {
        List<Double> pointsPerDay = new ArrayList<>();

        Map<Integer, List<Takeover>> takeoversPerDay = takeovers.stream().collect(Collectors.groupingBy(t -> roundCalculator.dayOfRound(t.getRoundId(), t.getTakeoverTime())));

        for (int i = 1; i < numberOfDaysInRoundYet + 1; i++) {
//            logger.info("Day {}", i);
            pointsPerDay.add(calculatePointsForDay(takeoversPerDay.get(i), now));
        }

        return pointsPerDay;
    }

    private Double calculatePointsForDay(List<Takeover> takeovers, ZonedDateTime now) {
        if (takeovers == null || takeovers.isEmpty()) {
            return 0.0;
        }

        return takeovers.stream().map(t -> t.pointsUntilNow(now)).collect(Collectors.summingDouble(Double::doubleValue));

//        int roundedSum = (int) Math.round(sum);
//
//        logger.info("Sum: {}, {}", roundedSum, sum);

    }





}
