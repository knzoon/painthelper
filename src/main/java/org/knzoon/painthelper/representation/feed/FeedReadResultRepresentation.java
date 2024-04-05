package org.knzoon.painthelper.representation.feed;

import java.util.List;

public class FeedReadResultRepresentation {
    private final Integer numberOfFeedsRead;
    private final Integer totalTakeoversCreated;
    private final Integer totalUniqueZonesUpdated;
    private final String totalTimeSpent;
    private final List<SingleFeedImportResultRepresentation> feedImportResults;

    public FeedReadResultRepresentation(Integer numberOfFeedsRead, Integer totalTakeoversCreated, Integer totalUniqueZonesUpdated, String totalTimeSpent, List<SingleFeedImportResultRepresentation> feedImportResults) {
        this.numberOfFeedsRead = numberOfFeedsRead;
        this.totalTakeoversCreated = totalTakeoversCreated;
        this.totalUniqueZonesUpdated = totalUniqueZonesUpdated;
        this.totalTimeSpent = totalTimeSpent;
        this.feedImportResults = feedImportResults;
    }

    public Integer getNumberOfFeedsRead() {
        return numberOfFeedsRead;
    }

    public Integer getTotalTakeoversCreated() {
        return totalTakeoversCreated;
    }

    public Integer getTotalUniqueZonesUpdated() {
        return totalUniqueZonesUpdated;
    }

    public String getTotalTimeSpent() {
        return totalTimeSpent;
    }

    public List<SingleFeedImportResultRepresentation> getFeedImportResults() {
        return feedImportResults;
    }

    public Integer totalFeeditemsRead() {
        if (feedImportResults.isEmpty()) {
            return 0;
        }

        return feedImportResults.stream().map(SingleFeedImportResultRepresentation::getFeedItemsRead).reduce(0, Integer::sum);
    }
}
