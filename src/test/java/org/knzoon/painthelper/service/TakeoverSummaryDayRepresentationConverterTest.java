package org.knzoon.painthelper.service;


import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.PointsInDay;
import org.knzoon.painthelper.representation.compare.TakeoverSummaryDayRepresentation;

import static org.assertj.core.api.Assertions.assertThat;

class TakeoverSummaryDayRepresentationConverterTest {

    @Test
    void first_day_of_round() {
        PointsInDay pointsInDay = new PointsInDay(97.8, 95.0, 2.8, 7, 1);
        TakeoverSummaryDayRepresentation representation = TakeoverSummaryDayRepresentationConverter.toRepresentation(0, pointsInDay);
        assertThat(representation.getWeek()).isEqualTo(1);
        assertThat(representation.getDay()).isEqualTo("Sunday");
        assertThat(representation.getPointsTotal()).isEqualTo(98);
        assertThat(representation.getPointsPph()).isEqualTo(3);
    }

    @Test
    void seventh_day_of_round() {
        PointsInDay pointsInDay = new PointsInDay(97.49, 95.0, 2.49, 7, 1);
        TakeoverSummaryDayRepresentation representation = TakeoverSummaryDayRepresentationConverter.toRepresentation(6, pointsInDay);
        assertThat(representation.getWeek()).isEqualTo(1);
        assertThat(representation.getDay()).isEqualTo("Saturday");
        assertThat(representation.getPointsTotal()).isEqualTo(97);
        assertThat(representation.getPointsPph()).isEqualTo(2);
    }

    @Test
    void eight_day_of_round() {
        PointsInDay pointsInDay = new PointsInDay(97.5, 95.0, 2.5, 0, 0);
        TakeoverSummaryDayRepresentation representation = TakeoverSummaryDayRepresentationConverter.toRepresentation(7, pointsInDay);
        assertThat(representation.getWeek()).isEqualTo(2);
        assertThat(representation.getDay()).isEqualTo("Sunday");
        assertThat(representation.getPointsTotal()).isEqualTo(98);
        assertThat(representation.getPointsPph()).isEqualTo(3);
    }

}