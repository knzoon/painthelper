package org.knzoon.painthelper.representation.compare;

import java.util.List;

public class GraphDatasetRepresentation {
    private final String label;
    private final List<GraphDatapointRepresentation> data;

    private final Integer totalPoints;

    public GraphDatasetRepresentation(String label, List<GraphDatapointRepresentation> data, Integer totalPoints) {
        this.label = label;
        this.data = data;
        this.totalPoints = totalPoints;
    }

    public static GraphDatasetRepresentation tom() {
        return new GraphDatasetRepresentation("unknown", List.of(), 0);
    }

    public String getLabel() {
        return label;
    }

    public List<GraphDatapointRepresentation> getData() {
        return data;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }
}
