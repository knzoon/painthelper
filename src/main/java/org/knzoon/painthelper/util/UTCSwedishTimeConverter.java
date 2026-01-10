package org.knzoon.painthelper.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class UTCSwedishTimeConverter {

    public static ZonedDateTime convert(ZonedDateTime utcDateTime) {
        return utcDateTime.withZoneSameInstant(ZoneId.of("Europe/Stockholm"));
    }
}
