package org.knzoon.painthelper.representation;

import java.time.ZonedDateTime;

public class BroadcastMessageRepresentation {
    private final String message;
    private final ZonedDateTime importNeededAfter;

    public BroadcastMessageRepresentation(String message, ZonedDateTime importNeededAfter) {
        this.message = message;
        this.importNeededAfter = importNeededAfter;
    }

    public String getMessage() {
        return message;
    }

    public ZonedDateTime getImportNeededAfter() {
        return importNeededAfter;
    }
}
