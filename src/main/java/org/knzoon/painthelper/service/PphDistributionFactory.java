package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.representation.compare.PphDistributionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PphDistributionFactory {
    public static PphDistributionRepresentation create(List<Takeover> takeovers) {
        Map<Integer, Long> pphDistribution = takeovers.stream()
                .collect(Collectors.groupingBy(Takeover::getPph, Collectors.counting()));

        return new PphDistributionRepresentation(
                pphDistribution.getOrDefault(1, 0L).intValue(),
                pphDistribution.getOrDefault(2, 0L).intValue(),
                pphDistribution.getOrDefault(3, 0L).intValue(),
                pphDistribution.getOrDefault(4, 0L).intValue(),
                pphDistribution.getOrDefault(5, 0L).intValue(),
                pphDistribution.getOrDefault(6, 0L).intValue(),
                pphDistribution.getOrDefault(7, 0L).intValue(),
                pphDistribution.getOrDefault(8, 0L).intValue(),
                pphDistribution.getOrDefault(9, 0L).intValue());
    }
}
