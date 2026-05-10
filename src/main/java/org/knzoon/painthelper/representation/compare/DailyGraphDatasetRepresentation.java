package org.knzoon.painthelper.representation.compare;

import java.util.List;

public record DailyGraphDatasetRepresentation(
        String label,
        List<Integer> data,
        String stack,
        Integer totalPoints) {

    public static DailyGraphDatasetRepresentation tom() {
        return new DailyGraphDatasetRepresentation("unknown", List.of(), "unknown", 0);
    }

    public static DailyGraphDatasetRepresentation takepointDataset(String username, List<Integer> data, Integer totalPoints) {
        return new DailyGraphDatasetRepresentation("Takepoints " + username, data, username, totalPoints);
    }

    public static DailyGraphDatasetRepresentation pphDataset(String username, List<Integer> data, Integer totalPoints) {
        return new DailyGraphDatasetRepresentation("Pph " + username, data, username, totalPoints);
    }
}
