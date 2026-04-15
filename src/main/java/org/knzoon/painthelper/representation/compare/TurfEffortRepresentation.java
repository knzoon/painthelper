package org.knzoon.painthelper.representation.compare;

public class TurfEffortRepresentation {
    private final String username;
    private final String timeSpent;
    private final Integer points;
    private final Integer takes;
    private final Integer routes;
    private final Integer takesInRoutes;
    private final Integer pointsByPph;
    private final PphDistributionRepresentation pphDistribution;

    private final PphDistributionRepresentation pphDistributionAllTakeovers;

    public TurfEffortRepresentation(String username,
                                    String timeSpent,
                                    Integer points,
                                    Integer takes,
                                    Integer routes,
                                    Integer takesInRoutes,
                                    Integer pointsByPph,
                                    PphDistributionRepresentation pphDistribution,
                                    PphDistributionRepresentation pphDistributionAllTakeovers) {
        this.username = username;
        this.timeSpent = timeSpent;
        this.points = points;
        this.takes = takes;
        this.routes = routes;
        this.takesInRoutes = takesInRoutes;
        this.pointsByPph = pointsByPph;
        this.pphDistribution = pphDistribution;
        this.pphDistributionAllTakeovers = pphDistributionAllTakeovers;
    }

    public String getTimeSpent() {
        return timeSpent;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getTakes() {
        return takes;
    }

    public String getUsername() {
        return username;
    }
    public Integer getRoutes() {
        return routes;
    }
    public Integer getTakesInRoutes() {
        return takesInRoutes;
    }
    public Integer getPointsByPph() {
        return pointsByPph;
    }
    public PphDistributionRepresentation getPphDistribution() {
        return pphDistribution;
    }
    public PphDistributionRepresentation getPphDistributionAllTakeovers() {
        return pphDistributionAllTakeovers;
    }
}
