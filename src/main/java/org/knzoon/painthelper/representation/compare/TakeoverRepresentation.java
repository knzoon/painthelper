package org.knzoon.painthelper.representation.compare;

public class TakeoverRepresentation {
    private final String takeoverTime;
    private final String zoneName;
    private final String areaName;
    private final Integer tp;
    private final Integer pph;
    private final String activity;
    private final Integer points;
    private final String duration;
    private final String previousUser;
    private final String nextUser;
    private final String assistingUser;
    private final Boolean accumulating;

    TakeoverRepresentation(String takeoverTime, String zoneName, String areaName, Integer tp, Integer pph, String activity, Integer points, String duration, String previousUser, String nextUser, String assistingUser, Boolean accumulating) {
        this.takeoverTime = takeoverTime;
        this.zoneName = zoneName;
        this.areaName = areaName;
        this.tp = tp;
        this.pph = pph;
        this.activity = activity;
        this.points = points;
        this.duration = duration;
        this.previousUser = previousUser;
        this.nextUser = nextUser;
        this.assistingUser = assistingUser;
        this.accumulating = accumulating;
    }

    public static TakeoverRepresentationBuilder builder() {
        return new TakeoverRepresentationBuilder();
    }

    public String getTakeoverTime() {
        return takeoverTime;
    }

    public String getZoneName() {
        return zoneName;
    }

    public String getAreaName() {
        return areaName;
    }

    public Integer getTp() {
        return tp;
    }

    public Integer getPph() {
        return pph;
    }

    public String getActivity() {
        return activity;
    }

    public Integer getPoints() {
        return points;
    }

    public String getDuration() {
        return duration;
    }

    public String getPreviousUser() {
        return previousUser;
    }

    public String getNextUser() {
        return nextUser;
    }

    public String getAssistingUser() {
        return assistingUser;
    }

    public Boolean getAccumulating() {
        return accumulating;
    }
}
