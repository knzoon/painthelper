package org.knzoon.painthelper.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
public class FeedReadInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long feedInfoId;

    private ZonedDateTime readTime;

    private Integer itemsInFeed;

    private Integer relevantItems;

    public FeedReadInfo() {
    }

    public FeedReadInfo(Long feedInfoId, ZonedDateTime readTime, Integer itemsInFeed, Integer relevantItems) {
        this.feedInfoId = feedInfoId;
        this.readTime = readTime;
        this.itemsInFeed = itemsInFeed;
        this.relevantItems = relevantItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFeedInfoId() {
        return feedInfoId;
    }

    public void setFeedInfoId(Long feedInfoId) {
        this.feedInfoId = feedInfoId;
    }

    public ZonedDateTime getReadTime() {
        return readTime;
    }

    public void setReadTime(ZonedDateTime readTime) {
        this.readTime = readTime;
    }

    public Integer getItemsInFeed() {
        return itemsInFeed;
    }

    public void setItemsInFeed(Integer itemsInFeed) {
        this.itemsInFeed = itemsInFeed;
    }

    public Integer getRelevantItems() {
        return relevantItems;
    }

    public void setRelevantItems(Integer relevantItems) {
        this.relevantItems = relevantItems;
    }
}
