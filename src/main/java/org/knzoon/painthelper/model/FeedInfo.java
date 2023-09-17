package org.knzoon.painthelper.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
public class FeedInfo {

    public static final String TAKEOVER_FEED = "takeover";
    public static final String ZONE_FEED = "zone";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String feedName;

    private ZonedDateTime latestFeedItemRead;

    public FeedInfo() {
    }

    public FeedInfo(String feedName) {
        this.feedName = feedName;
        this.latestFeedItemRead = ZonedDateTime.of(LocalDateTime.of(2022, 1, 1, 0, 0), ZoneId.of("UTC"));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeedName() {
        return feedName;
    }

    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public ZonedDateTime getLatestFeedItemRead() {
        return latestFeedItemRead;
    }

    public void setLatestFeedItemRead(ZonedDateTime latestFeedItemRead) {
        this.latestFeedItemRead = latestFeedItemRead;
    }
}
