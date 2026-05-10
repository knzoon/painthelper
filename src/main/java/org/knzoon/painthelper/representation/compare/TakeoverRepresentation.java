package org.knzoon.painthelper.representation.compare;

public record TakeoverRepresentation(
    String takeoverTime,
    String zoneName,
    String areaName,
    Integer tp,
    Integer pph,
    String activity,
    Integer points,
    String duration,
    String previousUser,
    String nextUser,
    String assistingUser,
    Boolean accumulating) {

    public static TakeoverRepresentationBuilder builder() {
        return new TakeoverRepresentationBuilder();
    }
}
