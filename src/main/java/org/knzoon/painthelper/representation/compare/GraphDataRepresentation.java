package org.knzoon.painthelper.representation.compare;

import java.util.List;

public class GraphDataRepresentation {
    private final GraphDatasetRepresentation cumulative;
    private final List<DailyGraphDatasetRepresentation> daily;

    public GraphDataRepresentation(GraphDatasetRepresentation cumulative, List<DailyGraphDatasetRepresentation> daily) {
        this.cumulative = cumulative;
        this.daily = daily;
    }

    public static GraphDataRepresentation tom() {
        return null;
    }

    public GraphDatasetRepresentation getCumulative() {
        return cumulative;
    }

    public List<DailyGraphDatasetRepresentation> getDaily() {
        return daily;
    }
}
