package org.knzoon.painthelper.representation.compare;

public class TakeoverRepresentationBuilder {
    private String takeoverTime;
    private String zoneName;
    private String areaName;
    private Integer tp;
    private Integer pph = 0;
    private String activity;
    private Integer points;
    private String duration;
    private String previousUser;
    private String nextUser;
    private String assistingUser;
    private Boolean accumulating = Boolean.FALSE;

    public TakeoverRepresentationBuilder withTakeoverTime(String takeoverTime) {
        this.takeoverTime = takeoverTime;
        return this;
    }

    public TakeoverRepresentationBuilder withZoneName(String zoneName) {
        this.zoneName = zoneName;
        return this;
    }

    public TakeoverRepresentationBuilder withAreaName(String areaName) {
        this.areaName = areaName;
        return this;
    }

    public TakeoverRepresentationBuilder withTp(Integer tp) {
        this.tp = tp;
        return this;
    }

    public TakeoverRepresentationBuilder withPph(Integer pph) {
        this.pph = pph;
        return this;
    }

    public TakeoverRepresentationBuilder withActivity(String activity) {
        this.activity = activity;
        return this;
    }

    public TakeoverRepresentationBuilder withPoints(Integer points) {
        this.points = points;
        return this;
    }

    public TakeoverRepresentationBuilder withDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public TakeoverRepresentationBuilder withPreviousUser(String previousUser) {
        this.previousUser = previousUser;
        return this;
    }

    public TakeoverRepresentationBuilder withNextUser(String nextUser) {
        this.nextUser = nextUser;
        return this;
    }

    public TakeoverRepresentationBuilder withAssistingUser(String assistingUser) {
        this.assistingUser = assistingUser;
        return this;
    }

    public TakeoverRepresentationBuilder withAccumulating(Boolean accumulating) {
        this.accumulating = accumulating;
        return this;
    }


    public TakeoverRepresentation build() {
        return new TakeoverRepresentation(takeoverTime, zoneName, areaName, tp, pph, activity, points, duration, previousUser, nextUser, assistingUser, accumulating);
    }

}
