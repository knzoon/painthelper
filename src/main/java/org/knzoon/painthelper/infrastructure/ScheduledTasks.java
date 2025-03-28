package org.knzoon.painthelper.infrastructure;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.model.feed.ImportFeedResultTotal;
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

    @Scheduled(fixedDelay = 60000)
    public void readTakeoverFeed() {
        ImportFeedResultTotal result = feedService.readTakeoversFromFeedBackup();
        if (result.anyFeedItemsRead()) {
            logger.info("Total imported takeovers: {} timespent: {} ", result.totalFeeditemsRead(), result.totalTimeSpentAsString());
        }
    }

    @Scheduled(fixedRate = 303333)
    public void readZoneFeed() {
        ImportFeedResultTotal result = feedService.readZonesFromFeedBackup();
        if (result.anyFeedItemsRead()) {
            logger.info("Total imported takeovers: {} timespent: {} ", result.totalFeeditemsRead(), result.totalTimeSpentAsString());
        }
    }
}
