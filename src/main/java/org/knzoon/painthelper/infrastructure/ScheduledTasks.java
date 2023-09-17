package org.knzoon.painthelper.infrastructure;

import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    private final FeedService feedService;

    @Autowired
    public ScheduledTasks(FeedService feedService) {
        this.feedService = feedService;
    }

    @Scheduled(fixedRate = 60000)
    public void readTakeoverFeed() {
        feedService.readFeedAndImportData(FeedInfo.TAKEOVER_FEED);
    }

    @Scheduled(fixedRate = 303333)
    public void readZoneFeed() {
        feedService.readFeedAndImportData(FeedInfo.ZONE_FEED);
    }
}
