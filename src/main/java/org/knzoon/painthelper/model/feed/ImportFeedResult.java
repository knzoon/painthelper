package org.knzoon.painthelper.model.feed;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public record ImportFeedResult(
        Integer feedItemsRead,
        Integer takeoversCreated,
        Integer uniqueZonesUpdated,
        Duration timeSpent) {

    public boolean hasFeedItemsBeenRead() {
        return feedItemsRead > 0;
    }

}
