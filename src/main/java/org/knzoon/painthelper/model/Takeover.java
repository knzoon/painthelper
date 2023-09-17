package org.knzoon.painthelper.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
public class Takeover {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TAKEOVER_SEQ")
    @SequenceGenerator(name = "TAKEOVER_SEQ", sequenceName = "takeover_sequence")
    private Long id;

    private Integer roundId;

    private TakeoverType type;

    private Long zoneId;

    private String zonetype;

    private ZonedDateTime takeoverTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer pph;

    private Integer tp;

    // previous take of this zone

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "previous_user_id")
    private User previousUser;

    // Only has values when Takeover is of type assist
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assisting_user_id")
    private User assistingUser;

    // next takeover of this zone, will be filled when creating that takeover

    private ZonedDateTime lostTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_user_id")
    private User nextUser;

    private static final int MIDDAY_HOUR = 12;
    private static final int MIDDAY_MINUTE = 0;
    private static final ZonedDateTime FIRST_DAY_OF_MONTH_FIRST_ROUND = ZonedDateTime.of(LocalDateTime.of(2010, 7, 1, MIDDAY_HOUR, MIDDAY_MINUTE), ZoneId.of("Europe/Stockholm"));


    public Takeover() {
    }

    public Takeover(Integer roundId, TakeoverType type, Long zoneId, String zonetype, ZonedDateTime takeoverTime, User user, Integer pph, Integer tp, User previousUser, User assistingUser) {
        this.roundId = roundId;
        this.type = type;
        this.zoneId = zoneId;
        this.zonetype = zonetype;
        this.takeoverTime = takeoverTime;
        this.user = user;
        this.pph = pph;
        this.tp = tp;
        this.previousUser = previousUser;
        this.assistingUser = assistingUser;
    }

    public void setNextInfo(User nextUser, ZonedDateTime takeoverTime) {
        this.nextUser = nextUser;
        this.lostTime = takeoverTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRoundId() {
        return roundId;
    }

    public void setRoundId(Integer roundId) {
        this.roundId = roundId;
    }

    public TakeoverType getType() {
        return type;
    }

    public void setType(TakeoverType type) {
        this.type = type;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public void setZoneId(Long zoneId) {
        this.zoneId = zoneId;
    }

    public String getZonetype() {
        return zonetype;
    }

    public void setZonetype(String zonetype) {
        this.zonetype = zonetype;
    }

    public ZonedDateTime getTakeoverTime() {
        return takeoverTime;
    }

    public void setTakeoverTime(ZonedDateTime takeoverTime) {
        this.takeoverTime = takeoverTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getPph() {
        return pph;
    }

    public void setPph(Integer pph) {
        this.pph = pph;
    }

    public Integer getTp() {
        return tp;
    }

    public void setTp(Integer tp) {
        this.tp = tp;
    }

    public User getPreviousUser() {
        return previousUser;
    }

    public void setPreviousUser(User previousUser) {
        this.previousUser = previousUser;
    }

    public User getAssistingUser() {
        return assistingUser;
    }

    public void setAssistingUser(User assistingUser) {
        this.assistingUser = assistingUser;
    }

    public ZonedDateTime getLostTime() {
        return lostTime;
    }

    public void setLostTime(ZonedDateTime lostTime) {
        this.lostTime = lostTime;
    }

    public User getNextUser() {
        return nextUser;
    }

    public void setNextUser(User nextUser) {
        this.nextUser = nextUser;
    }

    /// Copied from LeagueHelper
    public Double pointsUntilNow(ZonedDateTime now) {
        // Four cases
        // 1 Neutral zone
        // 2 Revisit
        // 3 Assist
        // 4 Regular zone

        if (isNeutralZone()) {
//            logger.info("tp: {}, pphPart: {}, 50, sum: {}", tp, pphPart(now),  tp + pphPart(now) + 50);
            return tp + pphPart(now) + 50;
        } else if (isRevisit()) {
            return tp / 2.0;
        } else if (type == TakeoverType.ASSIST) {
            return (double) tp;
        }

        return tp + pphPart(now);

    }

    boolean isNeutralZone() {
        return previousUser == null;
    }

    boolean isRevisit() {
        return user.equals(previousUser);
    }

    Double pphPart(ZonedDateTime now) {
        ZonedDateTime endTime = this.lostTime == null ? calculateEndTime(now) : this.lostTime;
        Duration duration = Duration.between(this.takeoverTime, endTime);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YY-MM-dd HH:mm:ss");
//        String durationFormatted = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
//        logger.info("takeoverId: {}, {} {} {}", id, takeoverTime.format(formatter), zoneId, durationFormatted);

        long seconds = duration.getSeconds();
        double hours = seconds / 3600.0;

        return hours * this.pph;
    }

    ZonedDateTime calculateEndTime(ZonedDateTime now) {
        ZonedDateTime endtime = endtimeForRound();
        return now.isBefore(endtime) ? now : endtime;
    }

    ZonedDateTime endtimeForRound() {
        ZonedDateTime startTimeNextRound = starttimeForRound(this.roundId + 1);
        return startTimeNextRound.minusSeconds(1);
    }

    ZonedDateTime starttimeForRound(Integer roundId) {
        ZonedDateTime firstDayOfMonthOfThisRound = FIRST_DAY_OF_MONTH_FIRST_ROUND.plusMonths(roundId - 1);
        int dayOfWeekForFirstInMonth = firstDayOfMonthOfThisRound.getDayOfWeek().getValue();
        return firstDayOfMonthOfThisRound.plusDays(7 - dayOfWeekForFirstInMonth);
    }


    ///
}
