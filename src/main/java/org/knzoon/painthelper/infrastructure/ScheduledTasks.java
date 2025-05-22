package org.knzoon.painthelper.infrastructure;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.model.feed.ImportFeedResultTotal;
import org.knzoon.painthelper.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);
    private final FeedService feedService;

    @Value("${feed.read.disable:false}")
    private String disableFeedread;

    @Autowired
    public ScheduledTasks(FeedService feedService) {
        this.feedService = feedService;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 10000)
    public void readTakeoverFeed() {
        if (readingFeedIsDisabled()) {
            logger.info("skipping takeover feed read due to env var {}", disableFeedread);
        } else {
            ImportFeedResultTotal result = feedService.readTakeoversFromFeedBackup();
            if (result.anyFeedItemsRead()) {
                logger.info("Total imported takeovers: {} timespent: {} ", result.totalFeeditemsRead(), result.totalTimeSpentAsString());
            }
        }
    }

    private boolean readingFeedIsDisabled() {
        return disableFeedread != null && disableFeedread.equals("true");
    }

    @Scheduled(fixedDelay = 300000, initialDelay = 20000)
    public void readZoneFeed() {
        if (readingFeedIsDisabled()) {
            logger.info("skipping zone feed read due to env var {}", disableFeedread);
        } else {
            ImportFeedResultTotal result = feedService.readZonesFromFeedBackup();
            if (result.anyFeedItemsRead()) {
                logger.info("Total imported zones: {} timespent: {} ", result.totalFeeditemsRead(), result.totalTimeSpentAsString());
            }
        }
    }
}
