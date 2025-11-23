package org.knzoon.painthelper.service;

import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.Route;
import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverType;
import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.model.Zone;
import org.knzoon.painthelper.representation.compare.TakeoverRepresentation;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TakeoverRepresentationConverterTest {
    private final TakeoverRepresentationConverter converter = new TakeoverRepresentationConverter();
    @Test
    void duration_less_than_one_day() {
        var from = LocalDateTime.now();
        var to = LocalDateTime.now().plusHours(23).plusMinutes(49).plusSeconds(15);
        Duration duration = Duration.between(from, to);
        assertThat(converter.formattedDuration(duration)).isEqualTo("23:49:15");
    }

    @Test
    void duration_more_than_one_day_but_less_than_two() {
        var from = LocalDateTime.now();
        var to = LocalDateTime.now().plusDays(1).plusHours(2).plusMinutes(05).plusSeconds(10);
        Duration duration = Duration.between(from, to);
        assertThat(converter.formattedDuration(duration)).isEqualTo("1 dagar 02:05:10");
    }

    @Test
    void correct_activity_when_takeover() {
        ZonedDateTime now = ZonedDateTime.now();
        User user = new User(313l, "john");
        Takeover takeover = new Takeover(185, TakeoverType.TAKEOVER, 42l, "Bridge", now.minusHours(1), user, 7, 95, null, null);
        Map<Long, Zone> zoneMap = new HashMap<>();
        TakeoverRepresentation representation = converter.toRepresentation(takeover, now, zoneMap);
        assertThat(representation.getActivity()).isEqualTo("TAKEOVER");
    }

    @Test
    void correct_activity_when_assist() {
        ZonedDateTime now = ZonedDateTime.now();
        User user = new User(313l, "john");
        User assistingUser = new User(666l, "jane");
        Takeover takeover = new Takeover(185, TakeoverType.ASSIST, 42l, "Bridge", now.minusHours(1), user, 7, 95, null, assistingUser);
        Map<Long, Zone> zoneMap = new HashMap<>();
        TakeoverRepresentation representation = converter.toRepresentation(takeover, now, zoneMap);
        assertThat(representation.getActivity()).isEqualTo("ASSIST");
    }

    @Test
    void correct_activity_when_revisit() {
        ZonedDateTime now = ZonedDateTime.now();
        User user = new User(313l, "john");
        Takeover takeover = new Takeover(185, TakeoverType.TAKEOVER, 42l, "Bridge", now.minusHours(1), user, 7, 95, user, null);
        Map<Long, Zone> zoneMap = new HashMap<>();
        TakeoverRepresentation representation = converter.toRepresentation(takeover, now, zoneMap);
        assertThat(representation.getActivity()).isEqualTo("REVISIT");
    }
}