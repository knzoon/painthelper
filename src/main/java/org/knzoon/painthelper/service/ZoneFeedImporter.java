package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.model.FeedInfoRepository;
import org.knzoon.painthelper.model.feed.ImportFeedResult;
import org.knzoon.painthelper.representation.turfapi.FeedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ZoneFeedImporter {

    private final FeedInfoRepository feedInfoRepository;
    private final ZoneUpdater zoneUpdater;

    @Autowired
    public ZoneFeedImporter(FeedInfoRepository feedInfoRepository, ZoneUpdater zoneUpdater) {
        this.feedInfoRepository = feedInfoRepository;
        this.zoneUpdater = zoneUpdater;
    }

    @Transactional
    public FeedInfo getFeedOrCreateIfNeccecery() {
        FeedInfo feedInfo = feedInfoRepository.findByFeedName(FeedInfo.ZONE_FEED);

        if (feedInfo == null) {
            feedInfo = new FeedInfo(FeedInfo.ZONE_FEED);
            feedInfoRepository.save(feedInfo);
        }

        return feedInfo;
    }

    @Transactional
    public ImportFeedResult importFeedItems(List<FeedItem> feedItems) {
        Instant startingImport = Instant.now();

        feedItems.stream().forEach(zoneUpdater::saveZoneIfNew);
        timeOfLastFeedItem(feedItems).ifPresent(this::updateLastReadFeedItemId);

        Instant endingImport = Instant.now();

        return new ImportFeedResult(feedItems.size(),
                0,
                0,
                Duration.between(startingImport, endingImport));
    }

    private Optional<ZonedDateTime> timeOfLastFeedItem(List<FeedItem> feedItems) {
        if (feedItems.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(feedItems.get(feedItems.size() - 1).getTime());
    }


    private void updateLastReadFeedItemId(ZonedDateTime takeovertimeOfLastFeedItem) {
        FeedInfo feedInfo = feedInfoRepository.findByFeedName(FeedInfo.ZONE_FEED);
        feedInfo.setLatestFeedItemRead(takeovertimeOfLastFeedItem);
    }


}
