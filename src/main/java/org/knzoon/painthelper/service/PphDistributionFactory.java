package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.Takeover;
import org.knzoon.painthelper.representation.compare.PphDistributionRepresentation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PphDistributionFactory {
    public static PphDistributionRepresentation createForUniqueZones(List<Takeover> takeovers) {
        Map<Integer, Long> pphDistribution = takeovers.stream()
                .map(t -> new MinimalZone(t.getZoneId(), t.getPph()))
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.groupingBy(MinimalZone::pph, Collectors.counting()));

        return toPphDistributionRepresentation(pphDistribution);
    }

    private static PphDistributionRepresentation toPphDistributionRepresentation(Map<Integer, Long> pphDistribution) {
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

    public static PphDistributionRepresentation createForAllTakeovers(List<Takeover> takeovers) {
        Map<Integer, Long> pphDistribution = takeovers.stream()
                .collect(Collectors.groupingBy(Takeover::getPph, Collectors.counting()));
        return toPphDistributionRepresentation(pphDistribution);
    }
    private record MinimalZone (Long zoneId, Integer pph){}
}
