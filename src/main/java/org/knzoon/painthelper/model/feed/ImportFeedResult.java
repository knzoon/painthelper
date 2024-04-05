package org.knzoon.painthelper.model.feed;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public record ImportFeedResult(
        UUID lastReadFeedItemId,
        Integer feedItemsRead,
        Integer takeoversCreated,
        Integer uniqueZonesUpdated,
        Duration timeSpent) {

    public Optional<UUID> getLastReadFeedItemId() {
        return Optional.ofNullable(lastReadFeedItemId);
    }

    public boolean hasFeedItemsBeenRead() {
        return feedItemsRead > 0;
    }
}
