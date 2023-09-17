package org.knzoon.painthelper.service;


import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RoundCalculatorTest {


    @Test
    public void isRound146() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 8, 30, 14, 5), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(146);
    }


    @Test
    public void isAlsoRound146() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 8, 28, 22, 55), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(146);
    }

    @Test
    public void whyDoesThisWorkAsAlsoRound146() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 8, 28, 21, 55), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(146);
    }


    @Test
    public void justAfterMiddayFirstSundayInJanuariIsRound139() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 1, 2, 11, 5), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(139);
    }


    @Test
    public void justBeforeMiddayFirstSundayInJanuariIsRound138() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 1, 2, 10, 55), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(138);
    }

    @Test
    public void justAfterMiddayFirstSundayInMayIsRound143() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 5, 1, 10, 5), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(143);
    }

    @Test
    public void justBeforeMiddayFirstSundayInMayIsRound142() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 5, 1, 9, 55), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(142);
    }

    @Test
    public void inTheMiddleOfJuneIsRound144() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 6, 21, 20, 0), ZoneId.of("UTC"));
        assertThat(roundCalculator.roundFromDateTime(dateTime)).isEqualTo(144);
    }


    @Test
    public void worksWithBothUtcAndEuropeStockholm() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTimeUtc = ZonedDateTime.of(LocalDateTime.of(2022, 1, 2, 11, 5), ZoneId.of("UTC"));
        ZonedDateTime dateTimeSwe = ZonedDateTime.of(LocalDateTime.of(2022, 1, 2, 12, 5), ZoneId.of("Europe/Stockholm"));

        assertThat(roundCalculator.roundFromDateTime(dateTimeUtc)).isEqualTo(139);
        assertThat(roundCalculator.roundFromDateTime(dateTimeSwe)).isEqualTo(139);
    }

    @Test
    public void augustEightIsNotBefore() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 8, 8, 9, 00), ZoneId.of("UTC"));
        assertThat(roundCalculator.beforeRoundstart(dateTime)).isEqualTo(false);
    }

    @Test
    public void mayForthIsNotBefore() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 5, 4, 9, 00), ZoneId.of("UTC"));
        assertThat(roundCalculator.beforeRoundstart(dateTime)).isEqualTo(false);
    }

    @Test
    public void juneFifth1405UTCIsNotBefore() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(2022, 6, 5, 10, 05), ZoneId.of("UTC"));
        assertThat(roundCalculator.beforeRoundstart(dateTime)).isEqualTo(false);
    }

    @Test
    public void round146EndsJustBeforeNoonSeptemberFourth() {
        RoundCalculator roundCalculator = new RoundCalculator();
        ZonedDateTime expectedTime = ZonedDateTime.of(LocalDateTime.of(2022, 9, 4, 11, 59, 59), ZoneId.of("Europe/Stockholm"));
        ZonedDateTime endtime = roundCalculator.endtimeForRound(146);
        assertThat(endtime).isEqualTo(expectedTime);
    }

}