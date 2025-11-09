package org.knzoon.painthelper.model;

import java.util.ArrayList;
import java.util.List;

public class RouteFactory {

    private RouteFactory() {

    }

    public static List<Route> from(List<Takeover> takeovers) {
        if (takeovers == null) {
            return List.of();
        }

        Route currentRoute = new Route();
        List<Route> routes = new ArrayList<>();

        for (Takeover takeover : takeovers) {
            if (currentRoute.shouldContain(takeover)) {
                currentRoute.add(takeover);
            } else {
                routes.add(currentRoute);
                currentRoute = new Route(takeover);
            }
        }

        if (!currentRoute.isEmpty()) {
            routes.add(currentRoute);
        }

        return routes;
    }
}
