package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.feed.ImportFeedResult;
import org.knzoon.painthelper.model.feed.ImprovedFeedItem;
import org.knzoon.painthelper.model.feed.ImprovedFeedItemRepository;
import org.knzoon.painthelper.model.feed.LastReadFeedItem;
import org.knzoon.painthelper.model.feed.LastReadFeedItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class TakeoverFeedImporter {
    private final ImprovedFeedItemRepository improvedFeedItemRepository;
    private final LastReadFeedItemRepository lastReadFeedItemRepository;
    private final ZoneUpdater zoneUpdater;
    private final TakeoverCreator takeoverCreator;
    private final UniqueZoneUpdater uniqueZoneUpdater;

    private Logger logger = LoggerFactory.getLogger(TakeoverFeedImporter.class);

    @Autowired
    public TakeoverFeedImporter(ImprovedFeedItemRepository improvedFeedItemRepository, LastReadFeedItemRepository lastReadFeedItemRepository, ZoneUpdater zoneUpdater, TakeoverCreator takeoverCreator, UniqueZoneUpdater uniqueZoneUpdater) {
        this.improvedFeedItemRepository = improvedFeedItemRepository;
        this.lastReadFeedItemRepository = lastReadFeedItemRepository;
        this.zoneUpdater = zoneUpdater;
        this.takeoverCreator = takeoverCreator;
        this.uniqueZoneUpdater = uniqueZoneUpdater;
    }

    @Transactional
    public UUID findLastReadFeedItemId() {
        return lastReadFeedItemRepository.findById(1L).map(LastReadFeedItem::getFeedItemId).orElseGet(() -> improvedFeedItemRepository.findFirstByOrderByOrderNumber().getId());
    }

    @Transactional()
    public ImportFeedResult importFeedFrom(UUID lastReadFeedItemId) {
        Instant startingImport = Instant.now();

        Map<Long, Long> importedUsers = uniqueZoneUpdater.getImportedUsers();
        List<ImprovedFeedItem> feedItems = readFeedFrom(lastReadFeedItemId);
        int takeoversCreated = 0;
        int uniqueZonesUpdated = 0;

        for (ImprovedFeedItem feedItem : feedItems) {
            zoneUpdater.handlePossibleUpdatedZone(feedItem);
            takeoversCreated += takeoverCreator.saveTakeovers(feedItem);
            uniqueZonesUpdated += uniqueZoneUpdater.updateUniqueZones(feedItem, importedUsers);
        }

        updateLastReadFeedItemId(feedItems);

        Instant endingImport = Instant.now();
        return new ImportFeedResult(lastReadFeedItemId(feedItems), feedItems.size(), takeoversCreated, uniqueZonesUpdated, Duration.between(startingImport, endingImport));
    }

    private List<ImprovedFeedItem> readFeedFrom(UUID lastReadFeedItemId) {
        ImprovedFeedItem lastReadFeedItem = improvedFeedItemRepository.findById(lastReadFeedItemId).orElseThrow(() -> new RuntimeException("FeedItemId does not exists"));
        return improvedFeedItemRepository.findTop200ByOrderNumberGreaterThanOrderByOrderNumber(lastReadFeedItem.getOrderNumber());
    }

    private void updateLastReadFeedItemId(List<ImprovedFeedItem> feedItems) {
        if (feedItems.isEmpty()) {
            return;
        }

        UUID lastReadFeedItemId = feedItems.get(feedItems.size() -1).getId();
        Optional<LastReadFeedItem> readFeedItem = lastReadFeedItemRepository.findById(1L);
        if (readFeedItem.isPresent()) {
            readFeedItem.get().setFeedItemId(lastReadFeedItemId);
        } else {
            lastReadFeedItemRepository.save(new LastReadFeedItem(1L, lastReadFeedItemId));
        }
     }


    private UUID lastReadFeedItemId(List<ImprovedFeedItem> feedItems) {
        if (feedItems.isEmpty()) {
            return null;
        }

        return feedItems.get(feedItems.size() - 1).getId();
    }

}
