package org.knzoon.painthelper.model.compare;

import org.knzoon.painthelper.model.User;

import java.time.Duration;

public record TakeoverTotalForZone(
        User user,
        Integer visits,
        Integer points,
        Duration duration) {

    static final TakeoverTotalForZone ZERO = new TakeoverTotalForZone(null, 0, 0, Duration.ZERO);

    TakeoverTotalForZone add(TakeoverTotalForZone total) {
        return new TakeoverTotalForZone(
                total.user,
                this.visits + total.visits,
                this.points + total.points,
                this.duration.plus(total.duration)
        );
    }
}
