package org.knzoon.painthelper.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import org.knzoon.painthelper.util.RoundCalculator;

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
    public PointsInDay pointsUntilNow(ZonedDateTime now) {
        // Five cases
        // 1 Neutral zone takeover
        // 2 Neutral zone assist
        // 3 Revisit
        // 4 Assist
        // 5 Regular takeover

        Integer accumulatingPph = accumulatingPph(now);
        Integer zonesLeft = accumulatingPph > 0 ? 1 : 0;

        if (isNeutralZone()) {
//            logger.info("tp: {}, pphPart: {}, 50, sum: {}", tp, pphPart(now),  tp + pphPart(now) + 50);
            return new PointsInDay(tp + pphPart(now) + 50, tp.doubleValue(), pphPart(now) + 50, accumulatingPph, zonesLeft);
        } else if (isRevisit()) {
            return new PointsInDay(tp / 2.0, tp/2.0, 0.0, accumulatingPph, zonesLeft);
        }

        return new PointsInDay(tp + pphPart(now), tp.doubleValue(), pphPart(now), accumulatingPph, zonesLeft);

    }

    boolean isNeutralZone() {
        return previousUser == null;
    }

    boolean isRevisit() {
        return user.equals(previousUser);
    }

    public Double pphPart(ZonedDateTime now) {
        if (type == TakeoverType.ASSIST || isRevisit()) {
            return 0.0;
        }

        ZonedDateTime endTime = calculateEndTime(now);
        Duration duration = Duration.between(this.takeoverTime, endTime);
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YY-MM-dd HH:mm:ss");
//        String durationFormatted = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutesPart(), duration.toSecondsPart());
//        logger.info("takeoverId: {}, {} {} {}", id, takeoverTime.format(formatter), zoneId, durationFormatted);

        long seconds = duration.getSeconds();
        double hours = seconds / 3600.0;

        return hours * this.pph;
    }

    Integer accumulatingPph(ZonedDateTime now) {
        if (type == TakeoverType.ASSIST || isRevisit()) {
            return 0;
        }

        return calculateEndTime(now).isBefore(now) ? 0 : this.pph;
    }

    ZonedDateTime calculateEndTime(ZonedDateTime now) {
        if (this.lostTime != null) {
            return this.lostTime;
        }

        return RoundCalculator.calculateEndTime(this.roundId, now);
    }

}
