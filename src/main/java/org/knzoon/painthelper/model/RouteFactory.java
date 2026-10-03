package org.knzoon.painthelper.model;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class RouteFactory {

    private RouteFactory() {

    }

    public static List<Route> from(ZonedDateTime now, List<Takeover> takeovers) {
        if (takeovers == null) {
            return List.of();
        }

        Route currentRoute = new Route(now);
        List<Route> routes = new ArrayList<>();

        for (Takeover takeover : takeovers) {
            if (currentRoute.shouldContain(takeover)) {
                currentRoute.add(takeover);
            } else {
                routes.add(currentRoute);
                currentRoute = new Route(now, takeover);
            }
        }

        if (!currentRoute.isEmpty()) {
            routes.add(currentRoute);
        }

        return routes;
    }
}
