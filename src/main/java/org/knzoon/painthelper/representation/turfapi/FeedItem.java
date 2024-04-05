package org.knzoon.painthelper.representation.turfapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.knzoon.painthelper.model.FeedInfo;
import org.knzoon.painthelper.model.ValidationException;
import org.knzoon.painthelper.model.Zone;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedItem {
    private String type;
    private ZonedDateTime time;
    private ZoneFeedItemPart zone;
    private List<UserMinimal> assists;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ZonedDateTime getTime() {
        return time;
    }

    public void setTime(ZonedDateTime time) {
        this.time = time;
    }

    public ZoneFeedItemPart getZone() {
        return zone;
    }

    public void setZone(ZoneFeedItemPart zone) {
        this.zone = zone;
    }

    public Long getZoneId() {
        return zone.getId();
    }

    public String getZoneType() {
        if (zone.getType() == null) {
            return null;
        }

        return zone.getType().getName();
    }

    public List<UserMinimal> getAssists() {
        return assists;
    }

    public void setAssists(List<UserMinimal> assists) {
        this.assists = assists;
    }

    public UserMinimal getTakeoverUser() {
        return zone.getCurrentOwner();
    }

    public UserMinimal getPreviousUser() {
        return zone.getPreviousOwner();
    }

    public List<Long> getAllUserIdInvolved() {
        List<Long> usersInvolved = new ArrayList<>();
        usersInvolved.add(zone.getCurrentOwner().getId());

        if (assists != null && !assists.isEmpty()) {
            usersInvolved.addAll(assists.stream().map(UserMinimal::getId).collect(Collectors.toList()));
        }

        return usersInvolved;
    }

    public Long getRegionId() {
        return zone.getRegion().getId();
    }

    public Integer getPointsPerHour() {
        return zone.getPointsPerHour();
    }

    public Integer getTakeoverPoints() {
        return zone.getTakeoverPoints();
    }

    public boolean equalsExistingZone(Zone zoneFromDB) {
        if (zone == null) {
            throw new ValidationException("Zone comparison done wrong");
        }

        return zone.equalsExistingZone(zoneFromDB);
    }

}
