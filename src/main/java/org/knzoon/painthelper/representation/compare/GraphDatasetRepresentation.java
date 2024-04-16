package org.knzoon.painthelper.representation.compare;

import java.util.List;

public class GraphDatasetRepresentation {
    private final String label;
    private final List<GraphDatapointRepresentation> data;

    public GraphDatasetRepresentation(String label, List<GraphDatapointRepresentation> data) {
        this.label = label;
        this.data = data;
    }

    public String getLabel() {
        return label;
    }

    public List<GraphDatapointRepresentation> getData() {
        return data;
    }
}
