package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.*;
import org.knzoon.painthelper.model.feed.ImportFeedResult;
import org.knzoon.painthelper.model.feed.ImportFeedResultTotal;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.knzoon.painthelper.representation.turfapi.UserMinimal;
import org.knzoon.painthelper.representation.turfapi.ZoneFeedItemPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.knzoon.painthelper.model.FeedInfo.TAKEOVER_FEED;
import static org.knzoon.painthelper.model.FeedInfo.ZONE_FEED;

@Component
public class FeedService {

    private final TurfApiEndpoint turfApiEndpoint;
    private final ZoneRepository zoneRepository;
    private final TakeoverFeedImporter takeoverFeedImporter;
    private final ZoneFeedImporter zoneFeedImporter;
    private final FeedbackupEndpoint feedbackupEndpoint;

    private Logger logger = LoggerFactory.getLogger(FeedService.class);

    @Autowired
    public FeedService(TurfApiEndpoint turfApiEndpoint,
                       ZoneRepository zoneRepository,
                       TakeoverFeedImporter takeoverFeedImporter,
                       ZoneFeedImporter zoneFeedImporter,
                       FeedbackupEndpoint feedbackupEndpoint) {
        this.turfApiEndpoint = turfApiEndpoint;
        this.zoneRepository = zoneRepository;
        this.takeoverFeedImporter = takeoverFeedImporter;
        this.zoneFeedImporter = zoneFeedImporter;
        this.feedbackupEndpoint = feedbackupEndpoint;
    }

    @Transactional(timeout = 600)
    public int importAllZonesFromWarded() {
        logger.info("starting importing all zones from turf-api");
        List<ZoneFeedItemPart> allZones = turfApiEndpoint.getAllZones();

        if (allZones.isEmpty()) {
            return 0;
        }

        logger.info("removing any zones imported earlier");
        zoneRepository.deleteAll();
        logger.info("start parsing returned zones");
        List<Zone> zonesToPersist = allZones.stream().map(this::createZone).collect(Collectors.toList());
        logger.info("persist all zones at once");
        zoneRepository.saveAll(zonesToPersist);

        return zonesToPersist.size();
    }

    private Zone createZone(ZoneFeedItemPart zoneFromApi) {
        return new Zone(zoneFromApi.getId(), zoneFromApi.getName(), zoneFromApi.getLatitude(), zoneFromApi.getLongitude(),
                zoneFromApi.getRegionId(), zoneFromApi.getRegionName(), zoneFromApi.getAreaId(), zoneFromApi.getAreaName(),
                zoneFromApi.getCountryCode());
    }

    public ImportFeedResultTotal readTakeoversFromFeedBackup() {
        ImportFeedResultTotal importFeedResultTotal = new ImportFeedResultTotal();
        ImportFeedResult currentImportFeedResult;

        do {
            currentImportFeedResult = readOneTakeoverFeed();
            importFeedResultTotal.addImportFeedResult(currentImportFeedResult);
            logger.info("Imported zones: {} timespent: {} ", currentImportFeedResult.feedItemsRead(), currentImportFeedResult.timeSpent());
            sleep();
        } while (currentImportFeedResult.feedItemsRead() > 950);

        return importFeedResultTotal;
    }

    private ImportFeedResult readOneTakeoverFeed() {
        FeedInfo feedInfo = takeoverFeedImporter.getFeedOrCreateIfNeccecery();
        List<FeedItem> allFeedItems = feedbackupEndpoint.readFeed(feedInfo);
        return takeoverFeedImporter.importFeedItems(allFeedItems);
    }

    private static void sleep() {
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            //do nothing
        }
    }

    public ImportFeedResultTotal readZonesFromFeedBackup() {
        ImportFeedResultTotal importFeedResultTotal = new ImportFeedResultTotal();
        FeedInfo feedInfo = zoneFeedImporter.getFeedOrCreateIfNeccecery();
        List<FeedItem> allFeedItems = feedbackupEndpoint.readFeed(feedInfo);
        importFeedResultTotal.addImportFeedResult(zoneFeedImporter.importFeedItems(allFeedItems));

        return importFeedResultTotal;
    }

}
