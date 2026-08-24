package org.knzoon.painthelper.representation.compare;

public record ZoneTakeoverRepresentation(
    String takeoverTime,
    String user,
    Integer points,
    String duration) {
}
