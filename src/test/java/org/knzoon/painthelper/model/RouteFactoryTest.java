package org.knzoon.painthelper.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RouteFactoryTest {

    @Test
    public void should_create_two_routes_since_takeovers_too_far_apart() {
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
        Takeover firstTakeover = TakeoverTestbuilder.builder()
                .withTakeoverTime(now.minusHours(2))
                .build();
        Takeover secondTakeover = TakeoverTestbuilder.builder()
                .withTakeoverTime(now.minusHours(1))
                .build();

        List<Route> routes = RouteFactory.from(now, List.of(firstTakeover, secondTakeover));
        assertThat(routes).hasSize(2);
    }

    @Test
    public void should_create_one_route_since_takeovers_so_close_in_time() {
        ZonedDateTime now = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("UTC"));
        Takeover firstTakeover = TakeoverTestbuilder.builder()
                .withTakeoverTime(now.minusMinutes(10))
                .build();
        Takeover secondTakeover = TakeoverTestbuilder.builder()
                .withTakeoverTime(now.minusMinutes(5))
                .build();

        List<Route> routes = RouteFactory.from(now, List.of(firstTakeover, secondTakeover));
        assertThat(routes).hasSize(1);
    }

}