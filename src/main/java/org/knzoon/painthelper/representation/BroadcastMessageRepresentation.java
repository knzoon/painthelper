package org.knzoon.painthelper.representation;

import java.time.ZonedDateTime;

public record BroadcastMessageRepresentation(
        String message,
        ZonedDateTime importNeededAfter) {
}
