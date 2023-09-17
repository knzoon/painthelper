package org.knzoon.painthelper.service;


import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.TakeoverNeoView;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PointsCalculatorTest {

    @Test
    public void simpleTest() {
        PointsCalculator pointsCalculator = new PointsCalculator();

        ZonedDateTime takeoverTime = ZonedDateTime.of(LocalDateTime.of(2022, 8, 30, 14, 5, 10), ZoneId.of("UTC"));
        ZonedDateTime endtime = ZonedDateTime.of(LocalDateTime.of(2022, 8, 30, 16, 5, 10), ZoneId.of("UTC"));

        DummyTakeover dummyTakeover = new DummyTakeover(takeoverTime, 80, 8);

        Integer points = pointsCalculator.pointsForTakeover(dummyTakeover, endtime);

        assertThat(points).isEqualTo(96);
    }




    private class DummyTakeover implements TakeoverNeoView {

        private final Timestamp takeoverTime;
        private final Integer tp;
        private final Integer pph;

        private DummyTakeover(ZonedDateTime takeoverTime, Integer tp, Integer pph) {
            this.takeoverTime = Timestamp.from(takeoverTime.toInstant());
            this.tp = tp;
            this.pph = pph;
        }

        @Override
        public Long getUserId() {
            return null;
        }

        @Override
        public Long getZoneId() {
            return null;
        }

        @Override
        public String getZoneName() {
            return null;
        }

        @Override
        public Timestamp getTakeoverTime() {
            return this.takeoverTime;
        }

        @Override
        public Timestamp getLostTime() {
            return null;
        }

        @Override
        public Integer getTp() {
            return this.tp;
        }

        @Override
        public Integer getPph() {
            return this.pph;
        }

        @Override
        public String getType() {
            return null;
        }

        @Override
        public Double getLatitude() {
            return null;
        }

        @Override
        public Double getLongitude() {
            return null;
        }
    }
}