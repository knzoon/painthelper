package org.knzoon.painthelper.representation.compare;

import java.util.List;

public record GraphDatasetRepresentation(
        String label,
        List<GraphDatapointRepresentation> data,
        Integer totalPoints) {

    public static GraphDatasetRepresentation tom() {
        return new GraphDatasetRepresentation("unknown", List.of(), 0);
    }
}
