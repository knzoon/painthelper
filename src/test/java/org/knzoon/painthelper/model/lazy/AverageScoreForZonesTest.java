package org.knzoon.painthelper.model.lazy;

import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverTestbuilder;
import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.model.ZoneTestbuilder;
import org.knzoon.painthelper.representation.lazy.LazyZoneRepresentation;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AverageScoreForZonesTest {

    private static final ZoneId zoneId = ZoneId.of("Europe/Stockholm");

    @Test
    public void vanillaCaseWorking() {
        Zone trollskogen = ZoneTestbuilder.builder().withId(100L).withName("Trollskogen").withLatitude(0.5).withLongitude(1.5).build();
        Zone lilaLek = ZoneTestbuilder.builder().withId(200L).withName("LilaLek").withLatitude(0.6).withLongitude(1.6).build();
        Set<Zone> zones = Set.of(trollskogen, lilaLek);

        User praktikus = new User(1L, "praktikus");
        User leilar = new User(2L, "Leilar");

        List<Takeover> takeovers = new ArrayList<>();
        takeovers.addAll(takeoversLilaLek(lilaLek, leilar, praktikus));
        takeovers.addAll(takeoversTrollskogen(trollskogen, leilar, praktikus));

        AverageScoreForZones averageScoreForZones = new AverageScoreForZones(takeovers, zones);
        List<LazyZoneRepresentation> zonesWithAverageScore = averageScoreForZones.getZonesWithAverageScore(hourMinute(17, 00));

        assertThat(zonesWithAverageScore).hasSize(2);
        if (zonesWithAverageScore.get(0).zoneName().equals("LilaLek")) {
            assertThat(zonesWithAverageScore.get(0).averageScore()).isBetween(144.9, 145.1);
            assertThat(zonesWithAverageScore.get(1).averageScore()).isBetween(129.9, 130.1);
        } else {
            assertThat(zonesWithAverageScore.get(0).averageScore()).isBetween(129.9, 130.1);
            assertThat(zonesWithAverageScore.get(1).averageScore()).isBetween(144.9, 145.1);

        }
    }

    private ZonedDateTime hourMinute(int hour, int minutes) {
        return ZonedDateTime.of(LocalDateTime.of(2026, Month.MARCH, 1, hour, minutes), zoneId);
    }

    private List<Takeover> takeoversLilaLek(Zone lilaLek, User leilar, User praktikus) {
        TakeoverTestbuilder takeoverLilaLekTestbuilder = TakeoverTestbuilder.builder().withZoneId(lilaLek.getId()).withTp(140).withPph(4);
        return List.of(
                takeoverLilaLekTestbuilder
                        .withUser(leilar)
                        .withTakeoverTime(hourMinute(13, 00))
                        .withNextInfo(praktikus, hourMinute(16, 15))
                        .build(),
                takeoverLilaLekTestbuilder
                        .withUser(praktikus)
                        .withTakeoverTime(hourMinute(16, 15))
                        .withPreviousUser(leilar)
                        .withNextInfo(leilar, hourMinute(16, 45))
                        .build(),
                takeoverLilaLekTestbuilder
                        .withUser(leilar)
                        .withTakeoverTime(hourMinute(16, 45))
                        .withPreviousUser(praktikus)
                        .build()
        );
    }

    private List<Takeover> takeoversTrollskogen(Zone trollskogen, User leilar, User praktikus) {
        TakeoverTestbuilder takeoverTrollskogenTestbuilder = TakeoverTestbuilder.builder().withZoneId(trollskogen.getId()).withTp(125).withPph(5);

        return List.of(
                takeoverTrollskogenTestbuilder
                        .withUser(praktikus)
                        .withTakeoverTime(hourMinute(13, 10))
                        .withNextInfo(leilar, hourMinute(14, 10))
                        .build(),
                takeoverTrollskogenTestbuilder
                        .withUser(leilar)
                        .withTakeoverTime(hourMinute(14, 10))
                        .withPreviousUser(praktikus)
                        .withNextInfo(praktikus, hourMinute(16, 10))
                        .build(),
                takeoverTrollskogenTestbuilder
                        .withUser(praktikus)
                        .withTakeoverTime(hourMinute(16, 10))
                        .withPreviousUser(leilar)
                        .build()
        );
    }
}