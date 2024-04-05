package org.knzoon.painthelper.infrastructure;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.representation.feed.FeedReadResultRepresentation;
import org.knzoon.painthelper.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final FeedService feedService;

    @Autowired
    public ScheduledTasks(FeedService feedService) {
        this.feedService = feedService;
    }

    @Scheduled(fixedDelay = 15000)
    public void readTakeoverFeed() {
        FeedReadResultRepresentation result = feedService.readFromInternalFeed();
        if (result.totalFeeditemsRead() > 0) {
            logger.info("imported zones: {} timespent: {} ", result.totalFeeditemsRead(), result.getTotalTimeSpent());
        }
    }

    @Scheduled(fixedRate = 303333)
    public void readZoneFeed() {
        feedService.readFeedAndImportData(FeedInfo.ZONE_FEED);
    }
}
