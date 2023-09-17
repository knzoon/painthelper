package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.model.TakeoverNeoView;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class PointsCalculator {


    public Integer pointsForTakeover(TakeoverNeoView takeover, ZonedDateTime endTime) {

        Duration duration = Duration.between(takeover.getTakeoverTimeConverted(), endTime);
        long seconds = duration.getSeconds();

        double hours = seconds / 3600.0;

        double pphPart = hours * takeover.getPph();

        Integer pointsFromPph = (int) pphPart;

        return takeover.getTp() + pointsFromPph;
    }

}
