package org.knzoon.painthelper.service;

import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.representation.turfapi.FeedItem;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackupEndpointTest {

    private final FeedbackupEndpoint feedbackupEndpoint = new FeedbackupEndpoint();

    @Test
    void can_fetch_takeovers_from_feedbackup() {
        FeedInfo feedInfo = new FeedInfo(FeedInfo.TAKEOVER_FEED);
        feedInfo.setLatestFeedItemRead(ZonedDateTime.of(LocalDateTime.of(2025, 2, 28, 13, 26, 0), ZoneId.of("UTC")));
        List<FeedItem> feedItems = feedbackupEndpoint.readFeed(feedInfo);
        assertThat(feedItems).isNotEmpty();
    }

}