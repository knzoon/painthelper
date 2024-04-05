package org.knzoon.painthelper.representation.feed;

public class SingleFeedImportResultRepresentation {
    private final Integer feedItemsRead;
    private final Integer takeoversCreated;
    private final Integer uniqueZonesUpdated;
    private final String timeSpent;

    public SingleFeedImportResultRepresentation(Integer feedItemsRead, Integer takeoversCreated, Integer uniqueZonesUpdated, String timeSpent) {
        this.feedItemsRead = feedItemsRead;
        this.takeoversCreated = takeoversCreated;
        this.uniqueZonesUpdated = uniqueZonesUpdated;
        this.timeSpent = timeSpent;
    }

    public Integer getFeedItemsRead() {
        return feedItemsRead;
    }

    public Integer getTakeoversCreated() {
        return takeoversCreated;
    }

    public Integer getUniqueZonesUpdated() {
        return uniqueZonesUpdated;
    }

    public String getTimeSpent() {
        return timeSpent;
    }
}
