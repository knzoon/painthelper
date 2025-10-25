package org.knzoon.painthelper.representation.compare;

import java.util.List;

public class GraphDataRepresentation {
    private final GraphDatasetRepresentation cumulative;
    private final List<DailyGraphDatasetRepresentation> daily;
    private final List<TakeoverSummaryDayRepresentation> takeoverSummaryDaily;

    public GraphDataRepresentation(GraphDatasetRepresentation cumulative,
                                   List<DailyGraphDatasetRepresentation> daily,
                                   List<TakeoverSummaryDayRepresentation> takeoverSummaryDaily) {
        this.cumulative = cumulative;
        this.daily = daily;
        this.takeoverSummaryDaily = takeoverSummaryDaily;
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

    public List<TakeoverSummaryDayRepresentation> getTakeoverSummaryDaily() {
        return takeoverSummaryDaily;
    }
}
