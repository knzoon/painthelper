package org.knzoon.painthelper.service;


import org.junit.jupiter.api.Test;
import org.knzoon.painthelper.model.Point;
import org.knzoon.painthelper.representation.UniqueZoneRepresentation;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PanToCalculatorTest {

    @Test
    public void shouldReturnCenterAccordingToOutmostZones() {
        PanToCalculator panToCalculator = new PanToCalculator();

        UniqueZoneRepresentation topLeftCorner = createZone(20.0, 10.0);
        UniqueZoneRepresentation topRightCorner = createZone(20.0, 30.0);
        UniqueZoneRepresentation bottomLeftCorner = createZone(-20.0, 10.0);
        UniqueZoneRepresentation bottomRightCorner = createZone(-20.0, 30.0);

        Point point = panToCalculator.panTo(List.of(topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner));
        assertThat(point.latitude()).isEqualTo(0);
        assertThat(point.longitude()).isEqualTo(20);

    }

    @Test
    public void shouldReturnSkewedCenterAccordingToOutmostZones() {
        PanToCalculator panToCalculator = new PanToCalculator();

        UniqueZoneRepresentation topLeftCorner = createZone(20.0, 10.0);
        UniqueZoneRepresentation topRightCorner = createZone(10.0, 30.0);
        UniqueZoneRepresentation bottomLeftCorner = createZone(-20.0, 10.0);
        UniqueZoneRepresentation bottomRightCorner = createZone(-20.0, 20.0);

        Point point = panToCalculator.panTo(List.of(topLeftCorner, topRightCorner, bottomLeftCorner, bottomRightCorner));
        assertThat(point.latitude()).isEqualTo(-2.5);
        assertThat(point.longitude()).isEqualTo(17.5);

    }


    private UniqueZoneRepresentation createZone(double latitude, double longitude) {
        return new UniqueZoneRepresentation("apa", "apa", latitude, longitude, 1);
    }
}