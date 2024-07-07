package org.knzoon.painthelper.representation.compare;

public class GraphDataRepresentation {
    private final GraphDatasetRepresentation cumulative;
    private final GraphDatasetRepresentation daily;

    public GraphDataRepresentation(GraphDatasetRepresentation cumulative, GraphDatasetRepresentation daily) {
        this.cumulative = cumulative;
        this.daily = daily;
    }

    public static GraphDataRepresentation tom() {
        return null;
    }

    public GraphDatasetRepresentation getCumulative() {
        return cumulative;
    }

    public GraphDatasetRepresentation getDaily() {
        return daily;
    }
}
