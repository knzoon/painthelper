package org.knzoon.painthelper.model;

import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.service.RoundCalculator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;

import static org.assertj.core.api.Assertions.assertThat;

class TakeoverTest {

    private static final User userA = new User(42L, "userA");
    private static final User userB = new User(313L, "userB");
    private Instant now;
    private ZonedDateTime nowAsDateTime;
    private ZonedDateTime takeoverTime;
    private Integer currentRound;

    private RoundCalculator roundCalculator = new RoundCalculator();

    @BeforeEach
    void setUp() {
        now = Instant.now();
        nowAsDateTime = ZonedDateTime.ofInstant(now, ZoneId.of("UTC"));
        takeoverTime = ZonedDateTime.ofInstant(now.minus(Duration.ofHours(1)), ZoneId.of("UTC"));
        currentRound = roundCalculator.roundFromDateTime(nowAsDateTime);
    }

    @Test
    void points_after_an_hour_neutral_zone_takeover() {
        Takeover takeover = new Takeover(currentRound, TakeoverType.TAKEOVER, 31342L, null, takeoverTime, userA, 9, 65, null, null);
        assertThat(takeover.pointsUntilNow(nowAsDateTime)).isBetween(123.8, 124.2);
    }

    @Test
    void points_after_an_hour_neutral_zone_assist() {
        Takeover takeover = new Takeover(currentRound, TakeoverType.ASSIST, 31342L, null, takeoverTime, userA, 9, 65, null, userB);
        assertThat(takeover.pointsUntilNow(nowAsDateTime)).isBetween(115.0, 115.0);
    }
    @Test
    void points_after_an_hour_revisit() {
        Takeover takeover = new Takeover(currentRound, TakeoverType.TAKEOVER, 31342L, null, takeoverTime, userA, 9, 65, userA, null);
        assertThat(takeover.pointsUntilNow(nowAsDateTime)).isBetween(32.3, 32.7);
    }
    @Test
    void points_after_an_hour_assist() {
        Takeover takeover = new Takeover(currentRound, TakeoverType.ASSIST, 31342L, null, takeoverTime, userA, 9, 65, userB, userB);
        assertThat(takeover.pointsUntilNow(nowAsDateTime)).isBetween(65.0, 65.0);
    }
    @Test
    void points_after_an_hour_takeover() {
        Takeover takeover = new Takeover(currentRound, TakeoverType.TAKEOVER, 31342L, null, takeoverTime, userA, 9, 65, userB, null);
        assertThat(takeover.pointsUntilNow(nowAsDateTime)).isBetween(73.8, 74.2);
    }


}