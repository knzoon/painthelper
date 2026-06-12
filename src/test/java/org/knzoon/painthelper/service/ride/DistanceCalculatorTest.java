package org.knzoon.painthelper.service.ride;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DistanceCalculatorTest {

    @Test
    void reasonableDistanceBetweenPumpStationMotBerga() {
        double pumpStationLat = 63.725126;
        double pumpStationLon = 20.294228;
        double motBergaLat = 63.740238;
        double motBergaLon = 20.287617;
        double distance = DistanceCalculator.kmBetweenPoints(pumpStationLat, pumpStationLon, motBergaLat, motBergaLon);
        assertThat(distance).isBetween(1.71, 1.72);
    }

    @Test
    void reasonableDistanceBetweenKontaktzonSpårzon() {
        double kontaktzonLat = 63.847679;
        double kontaktzonLon = 20.214438;
        double spårzonLat = 63.845093;
        double spårzonLon = 20.211541;
        double distance = DistanceCalculator.kmBetweenPoints(kontaktzonLat, kontaktzonLon, spårzonLat, spårzonLon);
        assertThat(distance).isBetween(0.32, 0.33);
    }

}