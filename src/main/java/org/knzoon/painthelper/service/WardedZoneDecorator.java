package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.ErrorZone;
import org.knzoon.painthelper.model.ErrorZoneRepository;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.model.ZoneInfo;
import org.knzoon.painthelper.model.ZoneRepository;
import org.knzoon.painthelper.model.dto.DecoratedWardedDataDTO;
import org.knzoon.painthelper.model.dto.DecoratedWardedZoneDTO;
import org.knzoon.painthelper.model.dto.WardedDataDTO;
import org.knzoon.painthelper.representation.turfapi.ZoneFeedItemPart;
import org.knzoon.painthelper.representation.warded.UniqueWardedZones;
import org.knzoon.painthelper.representation.warded.WardedZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class WardedZoneDecorator {

    private final TurfApiEndpoint turfApiEndpoint;
    private final ZoneRepository zoneRepository;
    private final ErrorZoneRepository errorZoneRepository;

    private Logger logger = LoggerFactory.getLogger(WardedZoneDecorator.class);

    @Autowired
    public WardedZoneDecorator(TurfApiEndpoint turfApiEndpoint, ZoneRepository zoneRepository, ErrorZoneRepository errorZoneRepository) {
        this.turfApiEndpoint = turfApiEndpoint;
        this.zoneRepository = zoneRepository;
        this.errorZoneRepository = errorZoneRepository;
    }

    DecoratedWardedDataDTO decorate(WardedDataDTO wardedDataDTO) {
        Long userId = turfApiEndpoint.getUserId(wardedDataDTO);
        String username = wardedDataDTO.getUsername();

        Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion = new HashMap<>();

        Map<String, Zone> zonesInDB = getZonesPresentInDB(wardedDataDTO.getUniqueWardedZones());
        Map<String, ZoneFeedItemPart> zonesFromApi = getZonesMissingInDBButPresentInApi(wardedDataDTO.getUniqueWardedZones(), zonesInDB);
        wardedDataDTO.getUniqueWardedZones().getFeatures().forEach(zone -> decorateZone(zone, zonesInDB, zonesFromApi, uniqueZonesPerRegion, userId));

        return new DecoratedWardedDataDTO(userId, username, uniqueZonesPerRegion);
    }

    private Map<String, Zone> getZonesPresentInDB(UniqueWardedZones wardedZones) {
        List<List<WardedZone>> zonesPartitioned = wardedZones.zonesPartitioned();
        return zonesPartitioned.stream().map(this::getZonesFromDB).flatMap(List::stream).collect(Collectors.toMap(Zone::getName, Function.identity()));
    }

    private List<Zone> getZonesFromDB(List<WardedZone> wardedZones) {
        Set<String> zoneNames = wardedZones.stream().map(WardedZone::zoneName).collect(Collectors.toSet());
        return zoneRepository.findByNameIn(zoneNames);
    }

    private Map<String, ZoneFeedItemPart> getZonesMissingInDBButPresentInApi(UniqueWardedZones uniqueWardedZones, Map<String, Zone> zonesInDB) {
        List<String> missingZones = uniqueWardedZones.zoneNames().stream().filter(name -> !zonesInDB.containsKey(name)).collect(Collectors.toList());
        logger.info("antal zooner som saknas i DB {}", missingZones.size());
        Map<String, ZoneFeedItemPart> zonesFromApi = turfApiEndpoint.fetchZonesFromTurfApi(missingZones).stream().collect(Collectors.toMap(ZoneFeedItemPart::getName, Function.identity()));
        logger.info("antal saknade som dock fanns i api {}", zonesFromApi.size());
        return zonesFromApi;
    }

    private void decorateZone(WardedZone wardedZone, Map<String, Zone> zonesInDB, Map<String, ZoneFeedItemPart> zonesFromApi, Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion, Long userId) {
        Integer takes = wardedZone.getTakes();
        String zoneName = wardedZone.zoneName();
        Zone zoneFromDB = zonesInDB.get(zoneName);
        ZoneFeedItemPart zoneFromApi = zonesFromApi.get(zoneName);

        if (zoneFromDB == null) {
            logger.info("zonen med namn: {} fanns ej i databasen", zoneName);
            if (zoneFromApi != null) {
                logger.info("zonen med namn: {} fanns dock i api", zoneName);
                Optional<Zone> possibleZoneFromDB = zoneRepository.findById(zoneFromApi.getId());
                if (possibleZoneFromDB.isPresent()) {
                    // Update zone in db with correct name
                    logger.info("zonen med namn {} fanns i DB men med annat namn som nu är uppdaterat", zoneName);
                    zoneFromDB = possibleZoneFromDB.get();
                    zoneFromDB.setName(zoneName);
                } else {
                    // New zone that for some reason haven't been read by feed
                    logger.info("zonen med namn {} fanns ej i databasen men i api", zoneName);
                    // TODO ta bort duplicering av metod create
                    Zone newZone = createZone(zoneFromApi);
                    zoneRepository.save(newZone);
                    zoneFromDB = newZone;
                }
            } else {
                logger.info("zonen med namn: {} fanns ej i databasen eller api", zoneName);
                ErrorZone errorZone = new ErrorZone(userId, zoneName, takes, wardedZone.getLongitude(), wardedZone.getLatitude(), ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
                errorZoneRepository.save(errorZone);
                return;
            }
        }

        ZoneInfo zone = new ZoneInfo(zoneFromDB.getId(), zoneName, zoneFromDB.getLongitude(), zoneFromDB.getLatitude(), zoneFromDB.getAreaId(), zoneFromDB.getAreaName());
        DecoratedWardedZoneDTO decoratedWardedZoneDTO = new DecoratedWardedZoneDTO(zone, zoneFromDB.getRegionName(), takes);
        List<DecoratedWardedZoneDTO> decoratedWardedZoneForRegion = uniqueZonesPerRegion.computeIfAbsent(zoneFromDB.getRegionId(), k -> new ArrayList<>());
        decoratedWardedZoneForRegion.add(decoratedWardedZoneDTO);
    }

    DecoratedWardedDataDTO decorateOld(WardedDataDTO wardedDataDTO) {

        Long userId = turfApiEndpoint.getUserId(wardedDataDTO);
        String username = wardedDataDTO.getUsername();
        Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion = new HashMap<>();

        List<List<WardedZone>> partitions = wardedDataDTO.getUniqueWardedZones().zonesPartitioned();
        partitions.stream().forEach(chunk -> decorateChunkOld(chunk, uniqueZonesPerRegion, userId));

        return new DecoratedWardedDataDTO(userId, username, uniqueZonesPerRegion);
    }

    private void decorateChunkOld(List<WardedZone> chunk, Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion, Long userId) {
        Set<String> zoneNames = chunk.stream().map(WardedZone::zoneName).collect(Collectors.toSet());
        List<Zone> zonesFromDB = zoneRepository.findByNameIn(zoneNames);
        Map<String, Zone> zonesFromDBMap = zonesFromDB.stream().collect(Collectors.toMap(Zone::getName, Function.identity()));
        chunk.stream().forEach(wardedZone -> decorateZoneOld(wardedZone, zonesFromDBMap, uniqueZonesPerRegion, userId));
    }

    private void decorateZoneOld(WardedZone wardedZone, Map<String, Zone> zonesFromDBMap, Map<Long, List<DecoratedWardedZoneDTO>> uniqueZonesPerRegion, Long userId) {
        Integer takes = wardedZone.getTakes();
        String zoneName = wardedZone.zoneName();
        Zone zoneFromDB = zonesFromDBMap.get(zoneName);

        if (zoneFromDB == null) {
            logger.info("zonen med namn: {} fanns ej i databasen", zoneName);
            List<ZoneFeedItemPart> zonesFromApi = turfApiEndpoint.fetchZonesFromTurfApi(Collections.singletonList(zoneName));
            if (zonesFromApi.size() == 1) {
                logger.info("zonen med namn: {} fanns dock i api", zoneName);
                ZoneFeedItemPart zoneFromApi = zonesFromApi.get(0);
                Optional<Zone> possibleZoneFromDB = zoneRepository.findById(zoneFromApi.getId());
                if (possibleZoneFromDB.isPresent()) {
                    // Update zone in db with correct name
                    zoneFromDB = possibleZoneFromDB.get();
                    zoneFromDB.setName(zoneName);
                } else {
                    // New zone that for some reason haven't been read by feed
                    logger.info("zonen med namn {} fanns ej i databasen men i api", zoneName);
                    // TODO ta bort duplicering av metod create
                    Zone newZone = createZone(zoneFromApi);
                    zoneRepository.save(newZone);
                    zoneFromDB = newZone;
                }
            } else {
                logger.info("zonen med namn: {} fanns ej i databasen eller api", zoneName);
                ErrorZone errorZone = new ErrorZone(userId, zoneName, takes, wardedZone.getLongitude(), wardedZone.getLatitude(), ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC")));
                errorZoneRepository.save(errorZone);
                return;
            }
        }

        ZoneInfo zone = new ZoneInfo(zoneFromDB.getId(), zoneName, zoneFromDB.getLongitude(), zoneFromDB.getLatitude(), zoneFromDB.getAreaId(), zoneFromDB.getAreaName());
        DecoratedWardedZoneDTO decoratedWardedZoneDTO = new DecoratedWardedZoneDTO(zone, zoneFromDB.getRegionName(), takes);
        List<DecoratedWardedZoneDTO> decoratedWardedZoneForRegion = uniqueZonesPerRegion.computeIfAbsent(zoneFromDB.getRegionId(), k -> new ArrayList<>());
        decoratedWardedZoneForRegion.add(decoratedWardedZoneDTO);
    }

    private Zone createZone(ZoneFeedItemPart zoneFromApi) {
        return new Zone(zoneFromApi.getId(), zoneFromApi.getName(), zoneFromApi.getLatitude(), zoneFromApi.getLongitude(),
                zoneFromApi.getRegionId(), zoneFromApi.getRegionName(), zoneFromApi.getAreaId(), zoneFromApi.getAreaName(),
                zoneFromApi.getCountryCode());
    }

}
