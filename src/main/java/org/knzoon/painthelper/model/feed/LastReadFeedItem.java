package org.knzoon.painthelper.model.feed;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class LastReadFeedItem {
    @Id
    private Long id;

    private UUID feedItemId;

    public LastReadFeedItem() {
    }

    public LastReadFeedItem(Long id, UUID feedItemId) {
        this.id = id;
        this.feedItemId = feedItemId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getFeedItemId() {
        return feedItemId;
    }

    public void setFeedItemId(UUID feedItemId) {
        this.feedItemId = feedItemId;
    }
}
