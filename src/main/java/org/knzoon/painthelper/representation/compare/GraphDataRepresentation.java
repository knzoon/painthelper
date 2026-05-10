package org.knzoon.painthelper.representation.compare;

import java.util.List;

public record GraphDataRepresentation(
        GraphDatasetRepresentation cumulative,
        List<DailyGraphDatasetRepresentation> daily,
        List<TakeoverSummaryDayRepresentation> takeoverSummaryDaily) {

    public static GraphDataRepresentation tom() {
        return null;
    }
}
