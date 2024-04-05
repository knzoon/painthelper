package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.UniqueZone;
import org.knzoon.painthelper.model.UniqueZoneRepository;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.model.ZoneRepository;
import org.knzoon.painthelper.model.ZoneUpdated;
import org.knzoon.painthelper.model.ZoneUpdatedRepository;
import org.knzoon.painthelper.model.feed.ImprovedFeedItem;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.knzoon.painthelper.representation.turfapi.ZoneFeedItemPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Component
public class ZoneUpdater {

    private final ZoneRepository zoneRepository;
    private final UniqueZoneRepository uniqueZoneRepository;
    private final ZoneUpdatedRepository zoneUpdatedRepository;


    private Logger logger = LoggerFactory.getLogger(ZoneUpdater.class);

    @Autowired
    public ZoneUpdater(ZoneRepository zoneRepository, UniqueZoneRepository uniqueZoneRepository, ZoneUpdatedRepository zoneUpdatedRepository) {
        this.zoneRepository = zoneRepository;
        this.uniqueZoneRepository = uniqueZoneRepository;
        this.zoneUpdatedRepository = zoneUpdatedRepository;
    }

    public void handlePossibleUpdatedZone(ImprovedFeedItem improvedFeedItem) {
        ZoneFeedItemPart zonefromFeed = improvedFeedItem.takeoverAsFeedItem().getZone();
        Optional<Zone> possibleZoneFromDB = zoneRepository.findById(zonefromFeed.getId());

        if (possibleZoneFromDB.isPresent()) {
            Zone zoneFromDB = possibleZoneFromDB.get();
            if (!zonefromFeed.equalsExistingZone(zoneFromDB)) {
                zoneFromDB.updateNameOrCoordinates(zonefromFeed.getName(), zonefromFeed.getLatitude(), zonefromFeed.getLongitude());
                updateUniqueZones(zonefromFeed);
                logUpdateInDB(zoneFromDB.getId());
            }
        } else {
            zoneRepository.save(createZone(zonefromFeed));
//            logger.info("Imported zone: {}, {} from takeoverfeed", zonefromFeed.getName(), zonefromFeed.getRegionName());
        }
    }

    private void updateUniqueZones(ZoneFeedItemPart zonefromFeed) {
        Iterable<UniqueZone> searchResult = uniqueZoneRepository.findByZoneZoneId(zonefromFeed.getId());
        StreamSupport.stream(searchResult.spliterator(), false).forEach(uz -> uz.updateNameOrCoordinates(zonefromFeed.getName(), zonefromFeed.getLatitude(), zonefromFeed.getLongitude()));
    }

    private void logUpdateInDB(Long zoneId) {
        zoneUpdatedRepository.save(new ZoneUpdated(zoneId, ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"))));
    }

    private Zone createZone(ZoneFeedItemPart zoneFromApi) {
        return new Zone(zoneFromApi.getId(), zoneFromApi.getName(), zoneFromApi.getLatitude(), zoneFromApi.getLongitude(),
                zoneFromApi.getRegionId(), zoneFromApi.getRegionName(), zoneFromApi.getAreaId(), zoneFromApi.getAreaName(),
                zoneFromApi.getCountryCode());
    }

}
