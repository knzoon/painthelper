package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.Point;
import org.knzoon.painthelper.representation.UniqueZoneRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PanToCalculator {

    public Point panTo(List<UniqueZoneRepresentation> zonesWithTakes) {
        Double averageLatitude = averageDoubleValue(zonesWithTakes.stream().map(UniqueZoneRepresentation::getLatitude).collect(Collectors.toList()));
        Double averageLongitude = averageDoubleValue(zonesWithTakes.stream().map(UniqueZoneRepresentation::getLongitude).collect(Collectors.toList()));
        return new Point(averageLatitude, averageLongitude);
    }

    private Double averageDoubleValue(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        if (values.size() == 1) {
            return values.get(0);
        }

        Double sum = values.stream().collect(Collectors.summingDouble(Double::doubleValue));

        return sum / values.size();
    }


}
