package org.knzoon.painthelper.util;

import java.time.Duration;

public class DurationFormatter {

    private DurationFormatter() {
    }

    public static String format(Duration duration) {
        if (duration.toDays() > 0) {
            return String.format("%d dagar %02d:%02d:%02d", duration.toDays(), duration.toHoursPart(), duration.toMinutesPart(), duration.toSecondsPart());
        }

        return String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
    }

}
