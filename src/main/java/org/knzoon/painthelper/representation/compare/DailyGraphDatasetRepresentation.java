package org.knzoon.painthelper.representation.compare;

import java.util.List;

public class DailyGraphDatasetRepresentation {

    private final String label;
    private final List<Integer> data;
    private final String stack;
    private final Integer totalPoints;

    public DailyGraphDatasetRepresentation(String label,
                                           List<Integer> data,
                                           String stack,
                                           Integer totalPoints) {
        this.label = label;
        this.data = data;
        this.stack = stack;
        this.totalPoints = totalPoints;
    }

    public static DailyGraphDatasetRepresentation tom() {
        return new DailyGraphDatasetRepresentation("unknown", List.of(), "unknown", 0);
    }

    public static DailyGraphDatasetRepresentation takepointDataset(String username, List<Integer> data, Integer totalPoints) {
        return new DailyGraphDatasetRepresentation("Takepoints " + username, data, username, totalPoints);
    }

    public static DailyGraphDatasetRepresentation pphDataset(String username, List<Integer> data, Integer totalPoints) {
        return new DailyGraphDatasetRepresentation("Pph " + username, data, username, totalPoints);
    }

    public String getLabel() {
        return label;
    }

    public List<Integer> getData() {
        return data;
    }

    public String getStack() {
        return stack;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }
}
