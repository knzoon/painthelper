package org.knzoon.painthelper.util;


import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DurationFormatterTest {

    @Test
    void duration_less_than_one_day() {
        var from = LocalDateTime.now();
        var to = LocalDateTime.now().plusHours(23).plusMinutes(49).plusSeconds(15);
        Duration duration = Duration.between(from, to);
        assertThat(DurationFormatter.format(duration)).isEqualTo("23:49:15");
    }

    @Test
    void duration_more_than_one_day_but_less_than_two() {
        var from = LocalDateTime.now();
        var to = LocalDateTime.now().plusDays(1).plusHours(2).plusMinutes(05).plusSeconds(10);
        Duration duration = Duration.between(from, to);
        assertThat(DurationFormatter.format(duration)).isEqualTo("1 dagar 02:05:10");
    }

}