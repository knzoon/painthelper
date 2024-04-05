package org.knzoon.painthelper.representation.feed;

import org.knzoon.painthelper.representation.turfapi.FeedItem;

import java.time.ZonedDateTime;
import java.util.UUID;

public class ImprovedFeedItemRepresentation {
    private final UUID id;

    private final Long orderNumber;

    private final ZonedDateTime takeoverTime;

    private final FeedItem originalTakeover;

    public ImprovedFeedItemRepresentation(UUID id, Long orderNumber, ZonedDateTime takeoverTime, FeedItem originalTakeover) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.takeoverTime = takeoverTime;
        this.originalTakeover = originalTakeover;
    }

    public UUID getId() {
        return id;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public ZonedDateTime getTakeoverTime() {
        return takeoverTime;
    }

    public FeedItem getOriginalTakeover() {
        return originalTakeover;
    }
}
