package org.knzoon.painthelper.service;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

@Component
public class RoundCalculator {

    private static final int START_ROUND = 139;
    private static final int CALCULATOR_STARTYEAR = 2022;
    private static final int MIDDAY_HOUR = 12;
    private static final int MIDDAY_MINUTE = 0;
    private static final ZonedDateTime FIRST_DAY_OF_MONTH_FIRST_ROUND = ZonedDateTime.of(LocalDateTime.of(2010, 7, 1, MIDDAY_HOUR, MIDDAY_MINUTE), ZoneId.of("Europe/Stockholm"));

    // TODO RoundCalculator can't be part of service package since it is needed from Takeover
    public int roundFromDateTime(ZonedDateTime dateTime) {
        ZonedDateTime dateTimeSwe = dateTime.withZoneSameInstant(ZoneId.of("Europe/Stockholm"));

        int yearsAfterStart = dateTimeSwe.getYear() - CALCULATOR_STARTYEAR;
        int roundBasedOnYear = START_ROUND + yearsAfterStart * 12;
        int roundOffsetBasedOnMonth = dateTimeSwe.getMonthValue() - 1;
        int calculatedRound = roundBasedOnYear + roundOffsetBasedOnMonth;

        if (beforeRoundstart(dateTime)) {
            return calculatedRound - 1;
        }

        return calculatedRound;
    }

    boolean beforeRoundstart(ZonedDateTime dateTime) {
        ZonedDateTime dateTimeSwe = dateTime.withZoneSameInstant(ZoneId.of("Europe/Stockholm"));
        ZonedDateTime sundayMiddaySwe = dateTimeSwe.with(ChronoField.DAY_OF_WEEK, 7).with(ChronoField.HOUR_OF_DAY, MIDDAY_HOUR).with(ChronoField.MINUTE_OF_HOUR, MIDDAY_MINUTE);

        if ((dateTimeSwe.getMonthValue() != sundayMiddaySwe.getMonthValue()) || (sundayMiddaySwe.getDayOfMonth() > 7)) {
            // We know we're on a week after the first
            return false;
        }

        return dateTimeSwe.isBefore(sundayMiddaySwe);
    }

    public ZonedDateTime calculateEndTime(Integer roundId, ZonedDateTime now) {
        ZonedDateTime endtime = endtimeForRound(roundId);
        return now.isBefore(endtime) ? now : endtime;
    }

    ZonedDateTime endtimeForRound(Integer roundId) {
        ZonedDateTime startTimeNextRound = starttimeForRound(roundId + 1);
        return startTimeNextRound.minusSeconds(1);
    }

    ZonedDateTime starttimeForRound(Integer roundId) {
        ZonedDateTime firstDayOfMonthOfThisRound = FIRST_DAY_OF_MONTH_FIRST_ROUND.plusMonths(roundId - 1);
        int dayOfWeekForFirstInMonth = firstDayOfMonthOfThisRound.getDayOfWeek().getValue();
        return firstDayOfMonthOfThisRound.plusDays(7 - dayOfWeekForFirstInMonth);
    }

    Integer dayOfRound(Integer roundId, ZonedDateTime takeovertime) {
        ZonedDateTime noonStartDate = starttimeForRound(roundId).with(ChronoField.HOUR_OF_DAY, MIDDAY_HOUR).with(ChronoField.MINUTE_OF_HOUR, MIDDAY_MINUTE);
        ZonedDateTime noonEndDate = takeovertime.with(ChronoField.HOUR_OF_DAY, MIDDAY_HOUR).with(ChronoField.MINUTE_OF_HOUR, MIDDAY_MINUTE);
        return (int) Duration.between(noonStartDate, noonEndDate).toDays() + 1;
    }

}
