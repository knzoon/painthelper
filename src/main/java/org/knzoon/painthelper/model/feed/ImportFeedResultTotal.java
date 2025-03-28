package org.knzoon.painthelper.model.feed;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ImportFeedResultTotal {
    private final List<ImportFeedResult> importFeedResults = new ArrayList<>();
    private int numberOfFeedsReads = 0;

    public void addImportFeedResult(ImportFeedResult importFeedResult) {
        if (importFeedResult.hasFeedItemsBeenRead()) {
            numberOfFeedsReads++;
            importFeedResults.add(importFeedResult);
        }
    }

    public List<ImportFeedResult> getImportFeedResults() {
        return importFeedResults;
    }

    public Integer getNumberOfFeedsReads() {
        return numberOfFeedsReads;
    }

    public Duration totalTimeSpent() {
        return importFeedResults.stream().map(ImportFeedResult::timeSpent).reduce(Duration.ZERO, Duration::plus);
    }

    public Integer totalTakeoversCreated() {
        return importFeedResults.stream().map(ImportFeedResult::takeoversCreated).reduce(0, Integer::sum);
    }

    public Integer totalUniqueZonesUpdated() {
        return importFeedResults.stream().map(ImportFeedResult::uniqueZonesUpdated).reduce(0, Integer::sum);
    }

    public boolean anyFeedItemsRead() {
        return !importFeedResults.isEmpty() && importFeedResults.get(0).hasFeedItemsBeenRead();
    }

    public Integer totalFeeditemsRead() {
        if (importFeedResults.isEmpty()) {
            return 0;
        }

        return importFeedResults.stream().map(ImportFeedResult::feedItemsRead).reduce(0, Integer::sum);
    }

    public String totalTimeSpentAsString() {
        return totalTimeSpent().toString();
    }
}
