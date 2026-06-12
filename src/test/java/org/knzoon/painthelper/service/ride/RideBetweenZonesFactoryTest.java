package org.knzoon.painthelper.service.ride;

import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.ride.RideBetweenZones;
import org.knzoon.painthelper.model.ride.TakeoverRideView;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RideBetweenZonesFactoryTest {

    @Test
    void reasonableSpeedBetweenYtterladanHolmsjön() {
        TestTakeover ytterladan = new TestTakeover(takeovertime(8, 57, 28), 63.769061, 20.386479);
        TestTakeover holmsjön = new TestTakeover(takeovertime(9, 0, 48), 63.757539, 20.386293);
        RideBetweenZones rideBetweenZones = RideBetweenZonesFactory.create(ytterladan, holmsjön, 191);
        assertThat(rideBetweenZones.getKmph()).isBetween(23.0, 23.1);
    }

    @Test
    void reasonableSpeedBetweenBroUndretBlueRoad() {
        TestTakeover broundret = new TestTakeover(takeovertime(10, 14, 8), 63.800989, 20.299411);
        TestTakeover blueroad = new TestTakeover(takeovertime(10, 15, 10), 63.804506, 20.297888);
        RideBetweenZones rideBetweenZones = RideBetweenZonesFactory.create(broundret, blueroad, 191);
        assertThat(rideBetweenZones.getKmph()).isBetween(23.1, 23.2);
    }

    @Test
    void reasonableSpeedBetweenOrangeStigUddaZon() {
        TestTakeover orangestig = new TestTakeover(takeovertime(9, 28, 50), 63.69346, 20.372737);
        TestTakeover uddazon = new TestTakeover(takeovertime(9, 30, 52), 63.689683, 20.375422);
        RideBetweenZones rideBetweenZones = RideBetweenZonesFactory.create(orangestig, uddazon, 191);
        assertThat(rideBetweenZones.getKmph()).isBetween(12.99, 13.0);
    }


    private ZonedDateTime takeovertime(int hour, int min, int sec) {
        return ZonedDateTime.of(LocalDateTime.of(2026, 6, 1, hour, min, sec), ZoneId.of("UTC"));
    }

    private class TestTakeover implements TakeoverRideView {
        private final Timestamp takeovertime;
        private final double latitude;
        private final double longitude;

        private TestTakeover(ZonedDateTime takeovertime, double latitude, double longitude) {
            this.takeovertime = Timestamp.from(takeovertime.toInstant());
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public Long getUserId() {
            return 42L;
        }

        @Override
        public Long getZoneId() {
            return 313L;
        }

        @Override
        public Timestamp getTakeoverTime() {
            return takeovertime;
        }

        @Override
        public Double getLatitude() {
            return latitude;
        }

        @Override
        public Double getLongitude() {
            return longitude;
        }
    }
}