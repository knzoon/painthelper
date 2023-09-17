package org.knzoon.painthelper.model.dto;

public class RelationCsvDTO {
    private final Long fromZoneId;
    private final Long toZoneId;
    private final Integer timeInSeconds;

    public RelationCsvDTO(Long fromZoneId, Long toZoneId, Integer timeInSeconds) {
        this.fromZoneId = fromZoneId;
        this.toZoneId = toZoneId;
        this.timeInSeconds = timeInSeconds;
    }

    public String getFromZoneId() {
        return String.valueOf(fromZoneId);
    }

    public String getToZoneId() {
        return String.valueOf(toZoneId);
    }

    public String getTimeInSeconds() {
        return String.valueOf(timeInSeconds);
    }
}
