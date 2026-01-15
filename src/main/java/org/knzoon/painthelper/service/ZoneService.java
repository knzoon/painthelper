package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.AreaView;
import org.knzoon.painthelper.model.BroadcastMessage;
import org.knzoon.painthelper.model.BroadcastMessageRepository;
import org.knzoon.painthelper.model.Point;
import org.knzoon.painthelper.model.RegionTakes;
import org.knzoon.painthelper.model.RegionTakesRepository;
import org.knzoon.painthelper.model.TakeoverRepository;
import org.knzoon.painthelper.model.TakesColorDistribution;
import org.knzoon.painthelper.model.UniqueZone;
import org.knzoon.painthelper.model.UniqueZoneRepository;
import org.knzoon.painthelper.model.UniqueZoneView;
import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.model.UserRepository;
import org.knzoon.painthelper.model.ValidationException;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.model.ZoneInfo;
import org.knzoon.painthelper.model.ZoneRepository;
import org.knzoon.painthelper.model.dto.DecoratedWardedDataDTO;
import org.knzoon.painthelper.model.dto.DecoratedWardedZoneDTO;
import org.knzoon.painthelper.model.dto.ImportResultWardedDTO;
import org.knzoon.painthelper.model.dto.RegionTakesDTO;
import org.knzoon.painthelper.model.dto.WardedDataDTO;
import org.knzoon.painthelper.model.dto.ZoneSearchParamsDTO;
import org.knzoon.painthelper.representation.AreaRepresentation;
import org.knzoon.painthelper.representation.BroadcastMessageRepresentation;
import org.knzoon.painthelper.representation.RegionTakesRepresentation;
import org.knzoon.painthelper.representation.TakesColorDistributionRepresentation;
import org.knzoon.painthelper.representation.UniqueZoneRepresentation;
import org.knzoon.painthelper.representation.UniqueZoneSearchresultRepresentation;
import org.knzoon.painthelper.util.RoundCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final PanToCalculator panToCalculator;

    private Logger logger = LoggerFactory.getLogger(ZoneService.class);

    @Autowired
    public ZoneService(WardedZoneDecorator wardedZoneDecorator, RegionTakesRepository regionTakesRepository,
                       UniqueZoneRepository uniqueZoneRepository, UserRepository userRepository,
                       ZoneRepository zoneRepository, BroadcastMessageRepository broadcastMessageRepository,
                       TakeoverRepository takeoverRepository, PanToCalculator panToCalculator) {
        this.wardedZoneDecorator = wardedZoneDecorator;
        this.regionTakesRepository = regionTakesRepository;
        this.uniqueZoneRepository = uniqueZoneRepository;
        this.userRepository = userRepository;
        this.zoneRepository = zoneRepository;
        this.broadcastMessageRepository = broadcastMessageRepository;
        this.takeoverRepository = takeoverRepository;
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
        Integer roundId = RoundCalculator.roundFromDateTime(ZonedDateTime.now());
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
        return new RegionTakesDTO(regionTakes,
                takesColorDistributionEntireRegion(regionTakes.getRegionId(), regionTakes.getId()),
                takesColorDistributionInRoundEntireRegion(regionTakes.getUserId(), regionTakes.getRegionId()));
    }

    private TakesColorDistributionRepresentation takesColorDistributionEntireRegion(Long regionId, Long regionTakesId) {
        // TODO This should be refactored using TakesColorDistribution
        Integer untaken = zoneRepository.countZonesByNotTakenAndRegionIdAndRegionTakesId(regionId, regionTakesId);
        Integer green = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakesId, 1, 1);
        Integer yellow = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakesId, 2, 10);
        Integer orange = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakesId, 11, 20);
        Integer red = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakesId, 21, 50);
        Integer purple = uniqueZoneRepository.countByRegionTakesIdAndTakesBetween(regionTakesId, 51, 100000);

        return new TakesColorDistributionRepresentation(untaken, green, yellow, orange, red, purple);
    }

    private TakesColorDistributionRepresentation takesColorDistributionInRoundEntireRegion(Long userId, Long regionId) {
        int roundId = RoundCalculator.roundFromDateTime(ZonedDateTime.now());

        Integer untaken = zoneRepository.countZonesByNotTakenAndUserIdAndRoundIdAndRegionId(userId, roundId, regionId);
        List<UniqueZoneView> uniqueZones = takeoverRepository.findUniqueZonesForUserRoundAndRegion(roundId, userId, regionId);
        TakesColorDistribution colorDistribution = new TakesColorDistribution(untaken, uniqueZones);

        return new TakesColorDistributionRepresentation(colorDistribution);
    }

    private RegionTakesRepresentation toRepresentation(RegionTakesDTO regionTakesDTO) {
        RegionTakes regionTakes = regionTakesDTO.getRegionTakes();
        return new RegionTakesRepresentation(regionTakes.getId(),
                regionTakes.getRegionName(),
                regionTakes.getRegionId(),
                regionTakes.getUserId(),
                regionTakesDTO.getTakesColorDistributionRepresentation(),
                regionTakesDTO.getRoundColorDistributionRepresentation());
    }

    @Transactional
    public List<AreaRepresentation> getDistinctAreasForRegionTakes(Long regionTakesId) {
        Optional<RegionTakes> regionTakesFromDB = regionTakesRepository.findById(regionTakesId);

        if (regionTakesFromDB.isEmpty()) {
            return List.of();
        }

        List<AreaView> areasWithTakenZones = uniqueZoneRepository.findDistinctAreasByTakenAndRegionTakesId(regionTakesId);
        Map<Long, AreaView> mappedTakenAreas = areasWithTakenZones.stream().collect(Collectors.toMap(AreaView::getAreaId, Function.identity()));

        RegionTakes regionTakes = regionTakesFromDB.get();
        List<AreaView> distinctAreasByRegionId = zoneRepository.findDistinctAreasByRegionId(regionTakes.getRegionId());

        List<AreaView> areasWithoutTakenZones = distinctAreasByRegionId.stream().filter(areaView -> hasNoTakenZones(areaView, mappedTakenAreas)).collect(Collectors.toList());

        List<AreaRepresentation> completeAreaList = new ArrayList<>();

        if (!areasWithTakenZones.isEmpty()) {
            completeAreaList.addAll(areasWithTakenZones.stream().map(areaView -> toRepresentation(areaView, regionTakes.getUserId(), regionTakesId, regionTakes.getRegionId())).collect(Collectors.toList()));
        }

        completeAreaList.addAll(areasWithoutTakenZones.stream().map(areaView -> toRepresentation(areaView, regionTakes.getUserId(), regionTakesId, regionTakes.getRegionId())).collect(Collectors.toList()));

        return completeAreaList;
    }

    private boolean hasNoTakenZones(AreaView areaView, Map<Long, AreaView> mapOfTaken) {
        return !mapOfTaken.containsKey(areaView.getAreaId());
    }

    private AreaRepresentation toRepresentation(AreaView areaView, Long userId, Long regionTakesId, Long regionId) {
        String areaString = areaView.getArea() + " (" + areaView.getAntal() + ")";
        boolean neverTaken = areaView.getAntal() < 1;

        return new AreaRepresentation(areaView.getAreaId(),
                    areaString,
                    takesColorDistributionAreaOfRegion(regionId, regionTakesId, areaView.getAreaId(), neverTaken),
                    takesColorDistributionInRoundAreaOfRegion(userId, regionId, areaView.getAreaId(), neverTaken));
    }

    private TakesColorDistributionRepresentation takesColorDistributionAreaOfRegion(Long regionId, Long regionTakesId, Long areaId, boolean neverTaken) {
        Integer untaken = zoneRepository.countZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(regionId, regionTakesId, areaId);

        if (neverTaken) {
            return new TakesColorDistributionRepresentation(untaken, 0, 0, 0, 0, 0);
        }

        // TODO This should be refactored using TakesColorDistribution
        Integer green = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaId, 1, 1);
        Integer yellow = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaId, 2, 10);
        Integer orange = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaId, 11, 20);
        Integer red = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaId, 21, 50);
        Integer purple = uniqueZoneRepository.countByRegionTakesIdAndZoneAreaIdAndTakesBetween(regionTakesId, areaId, 51, 100000);

        return new TakesColorDistributionRepresentation(untaken, green, yellow, orange, red, purple);
    }

    private TakesColorDistributionRepresentation takesColorDistributionInRoundAreaOfRegion(Long userId, Long regionId, Long areaId, boolean neverTaken) {
        int roundId = RoundCalculator.roundFromDateTime(ZonedDateTime.now());
        Integer untaken = zoneRepository.countZonesByNotTakenAndUserIdAndRoundIdAndRegionIdAndAreaId(userId, roundId, regionId, areaId);

        if (neverTaken) {
            return new TakesColorDistributionRepresentation(untaken, 0, 0, 0, 0, 0);
        }

        List<UniqueZoneView> uniqueZones = takeoverRepository.findUniqueZonesForUserRoundAreaAndRegion(roundId, userId, regionId, areaId);
        TakesColorDistribution colorDistribution = new TakesColorDistribution(untaken, uniqueZones);

        return new TakesColorDistributionRepresentation(colorDistribution);
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
}
