package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.ErrorTakeover;
import org.knzoon.painthelper.model.ErrorTakeoverRepository;
import org.knzoon.painthelper.model.RegionTakes;
import org.knzoon.painthelper.model.RegionTakesRepository;
import org.knzoon.painthelper.model.UniqueZone;
import org.knzoon.painthelper.model.UniqueZoneRepository;
import org.knzoon.painthelper.model.UserIdView;
import org.knzoon.painthelper.model.ZoneInfo;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.knzoon.painthelper.representation.turfapi.ZoneFeedItemPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UniqueZoneUpdater {

    private final RegionTakesRepository regionTakesRepository;
    private final UniqueZoneRepository uniqueZoneRepository;
    private final ErrorTakeoverRepository errorTakeoverRepository;

    private Logger logger = LoggerFactory.getLogger(UniqueZoneUpdater.class);

    @Autowired
    public UniqueZoneUpdater(RegionTakesRepository regionTakesRepository, UniqueZoneRepository uniqueZoneRepository, ErrorTakeoverRepository errorTakeoverRepository) {
        this.regionTakesRepository = regionTakesRepository;
        this.uniqueZoneRepository = uniqueZoneRepository;
        this.errorTakeoverRepository = errorTakeoverRepository;
    }

    public int updateUniqueZones(FeedItem feedItem, Map<Long, Long> importedUsers) {
        List<Long> allUserIdInvolved = feedItem.getAllUserIdInvolved();
        Long regionId = feedItem.getRegionId();
        ZoneFeedItemPart zone = feedItem.getZone();
        ZonedDateTime takeoverTime = feedItem.getTime();

        return allUserIdInvolved.stream()
                .map(id -> importDataForSpecificUser(id, regionId, zone, takeoverTime, importedUsers))
                .collect(Collectors.summingInt(Integer::intValue));
    }

    private int importDataForSpecificUser(Long userId, Long regionId, ZoneFeedItemPart zone, ZonedDateTime takeoverTime, Map<Long, Long> importedUsers) {

        try {
            RegionTakes regionTakes = regionTakesRepository.findByUserIdAndRegionId(userId, regionId);

            if (regionTakes != null) {
                updateUniqueZone(zone, regionTakes);
                return 1;
            }

            if (importedUsers.containsKey(userId)) {
                createNewRegionTakes(zone.getRegionName(), regionId, userId);
                regionTakes = regionTakesRepository.findByUserIdAndRegionId(userId, regionId);
                updateUniqueZone(zone, regionTakes);
                return 1;
            }

        } catch (Exception e) {
            logger.error("Failed to update unique zone for userId: {}, zoneId: {}", userId, zone.getId());
            errorTakeoverRepository.save(new ErrorTakeover(userId, zone.getId(), takeoverTime, ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))));
        }

        return 0;
    }

    private void updateUniqueZone(ZoneFeedItemPart zone, RegionTakes regionTakes) {
        UniqueZone uniqueZone = getUniqueZoneOrCreateIfNeccecery(regionTakes, zone);
        uniqueZone.addTake();
        uniqueZoneRepository.save(uniqueZone);
    }

    private UniqueZone getUniqueZoneOrCreateIfNeccecery(RegionTakes regionTakes, ZoneFeedItemPart zoneFromFeed) {
        UniqueZone uniqueZone = uniqueZoneRepository.findByRegionTakesIdAndZoneZoneId(regionTakes.getId(), zoneFromFeed.getId());

        if (uniqueZone == null) {
            ZoneInfo zone = new ZoneInfo(zoneFromFeed.getId(), zoneFromFeed.getName(), zoneFromFeed.getLongitude(), zoneFromFeed.getLatitude(), zoneFromFeed.getAreaId(), zoneFromFeed.getAreaName());
            uniqueZone = new UniqueZone(0, zone, regionTakes);
        }

        return uniqueZone;
    }

    private void createNewRegionTakes(String regionName, Long regionId, Long userId) {
        RegionTakes newRegionTakes = new RegionTakes(regionName, regionId, userId);
        regionTakesRepository.save(newRegionTakes);
        logger.info("Skapar en regionsdatahållare för userId {} och region {}", userId, regionName);
    }

    public Map<Long, Long> getImportedUsers() {
        List<UserIdView> usersOfInterest = regionTakesRepository.findDistinctUsers();
        return usersOfInterest.stream().collect(Collectors.toMap(UserIdView::getUserId, UserIdView::getUserId));
    }


}
