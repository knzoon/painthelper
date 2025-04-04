package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.model.FeedInfoRepository;
import org.knzoon.painthelper.model.feed.ImportFeedResult;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TakeoverFeedImporter {
    private Logger logger = LoggerFactory.getLogger(TakeoverFeedImporter.class);

    private final FeedInfoRepository feedInfoRepository;
    private final ZoneUpdater zoneUpdater;
    private final TakeoverCreator takeoverCreator;
    private final UniqueZoneUpdater uniqueZoneUpdater;


    @Autowired
    public TakeoverFeedImporter(FeedInfoRepository feedInfoRepository, ZoneUpdater zoneUpdater, TakeoverCreator takeoverCreator, UniqueZoneUpdater uniqueZoneUpdater) {
        this.feedInfoRepository = feedInfoRepository;
        this.zoneUpdater = zoneUpdater;
        this.takeoverCreator = takeoverCreator;
        this.uniqueZoneUpdater = uniqueZoneUpdater;
    }

    @Transactional
    public FeedInfo getFeedOrCreateIfNeccecery() {
        FeedInfo feedInfo = feedInfoRepository.findByFeedName(FeedInfo.TAKEOVER_FEED);

        if (feedInfo == null) {
            feedInfo = new FeedInfo(FeedInfo.TAKEOVER_FEED);
            feedInfoRepository.save(feedInfo);
        }

        return feedInfo;
    }

    @Transactional
    public ImportFeedResult importFeedItems(List<FeedItem> feedItems) {
        Instant startingImport = Instant.now();

        Map<Long, Long> importedUsers = uniqueZoneUpdater.getImportedUsers();
        int takeoversCreated = 0;
        int uniqueZonesUpdated = 0;

        for (FeedItem feedItem : feedItems) {
            zoneUpdater.handlePossibleUpdatedZone(feedItem);
            takeoversCreated += takeoverCreator.saveTakeovers(feedItem);
            uniqueZonesUpdated += uniqueZoneUpdater.updateUniqueZones(feedItem, importedUsers);
        }

        Optional<ZonedDateTime> takeovertimeOfLastFeedItem = takeovertimeOfLastFeedItem(feedItems);

        takeovertimeOfLastFeedItem.ifPresent(this::updateLastReadFeedItemId);

        Instant endingImport = Instant.now();
        return new ImportFeedResult(feedItems.size(),
                takeoversCreated,
                uniqueZonesUpdated,
                Duration.between(startingImport, endingImport));
    }

    private Optional<ZonedDateTime> takeovertimeOfLastFeedItem(List<FeedItem> feedItems) {
        if (feedItems.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(feedItems.get(feedItems.size() - 1).getTime());
    }

    private void updateLastReadFeedItemId(ZonedDateTime takeovertimeOfLastFeedItem) {
        FeedInfo feedInfo = feedInfoRepository.findByFeedName(FeedInfo.TAKEOVER_FEED);
//        logger.info("Takeovertime of last feed item {}", takeovertimeOfLastFeedItem);
        feedInfo.setLatestFeedItemRead(takeovertimeOfLastFeedItem);
    }

}
