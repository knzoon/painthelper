package org.knzoon.painthelper.service;

import org.knzoon.painthelper.model.AreaView;
import org.knzoon.painthelper.model.RegionTakes;
import org.knzoon.painthelper.model.TakeoverRepository;
import org.knzoon.painthelper.model.TakesColorDistribution;
import org.knzoon.painthelper.model.UniqueZone;
import org.knzoon.painthelper.model.UniqueZoneRepository;
import org.knzoon.painthelper.model.UniqueZoneView;
import org.knzoon.painthelper.model.UniqueZoneViewTakesOnly;
import org.knzoon.painthelper.model.ZoneRepository;
import org.knzoon.painthelper.model.dto.AreaDTO;
import org.knzoon.painthelper.model.dto.RegionTakesDTO;
import org.knzoon.painthelper.util.RoundCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TakesColorDistributionDecorator {

    private final ZoneRepository zoneRepository;
    private final UniqueZoneRepository uniqueZoneRepository;
    private final TakeoverRepository takeoverRepository;

    @Autowired
    public TakesColorDistributionDecorator(ZoneRepository zoneRepository, UniqueZoneRepository uniqueZoneRepository, TakeoverRepository takeoverRepository) {
        this.zoneRepository = zoneRepository;
        this.uniqueZoneRepository = uniqueZoneRepository;
        this.takeoverRepository = takeoverRepository;
    }

    public RegionTakesDTO decorateRegionTakes(RegionTakes regionTakes) {
        return new RegionTakesDTO(regionTakes,
                takesColorDistributionEntireRegion(regionTakes.getRegionId(), regionTakes.getId()),
                takesColorDistributionInRoundEntireRegion(regionTakes.getUserId(), regionTakes.getRegionId()));
    }

    private TakesColorDistribution takesColorDistributionEntireRegion(Long regionId, Long regionTakesId) {
        Integer untaken = zoneRepository.countZonesByNotTakenAndRegionIdAndRegionTakesId(regionId, regionTakesId);
        List<UniqueZone> uniqueZones = uniqueZoneRepository.findByRegionTakesIdAndTakesBetweenOrderByTakesDescZoneAreaAscZoneNameAsc(regionTakesId, 1, 1000000);
        return new TakesColorDistribution(untaken, takesInfoFromUniqueZones(uniqueZones));
    }

    private List<UniqueZoneViewTakesOnly> takesInfoFromUniqueZones(List<UniqueZone> uniqueZones) {
        return uniqueZones.stream().map(uniqueZone -> new UniqueZoneViewTakesOnly(uniqueZone.getTakes())).collect(Collectors.toList());
    }

    private TakesColorDistribution takesColorDistributionInRoundEntireRegion(Long userId, Long regionId) {
        int roundId = RoundCalculator.roundFromDateTime(ZonedDateTime.now());

        Integer untaken = zoneRepository.countZonesByNotTakenAndUserIdAndRoundIdAndRegionId(userId, roundId, regionId);
        List<UniqueZoneView> uniqueZones = takeoverRepository.findUniqueZonesForUserRoundAndRegion(roundId, userId, regionId);
        return new TakesColorDistribution(untaken, uniqueZones);
    }

    public AreaDTO decorateArea(AreaView areaView, RegionTakes regionTakes) {
        boolean neverTaken = areaView.getAntal() < 1;

        return new AreaDTO(areaView,
                takesColorDistributionAreaOfRegion(regionTakes.getRegionId(), regionTakes.getId(), areaView.getAreaId(), neverTaken),
                takesColorDistributionInRoundAreaOfRegion(regionTakes.getUserId(), regionTakes.getRegionId(), areaView.getAreaId(), neverTaken));
    }

    private TakesColorDistribution takesColorDistributionAreaOfRegion(Long regionId, Long regionTakesId, Long areaId, boolean neverTaken) {
        Integer untaken = zoneRepository.countZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(regionId, regionTakesId, areaId);

        if (neverTaken) {
            return new TakesColorDistribution(untaken, List.of());
        }

        List<UniqueZone> uniqueZones = uniqueZoneRepository.findByRegionTakesIdAndZoneAreaIdAndTakesBetweenOrderByTakesDescZoneNameAsc(regionTakesId, areaId, 1, 1000000);
        return new TakesColorDistribution(untaken, takesInfoFromUniqueZones(uniqueZones));
    }

    private TakesColorDistribution takesColorDistributionInRoundAreaOfRegion(Long userId, Long regionId, Long areaId, boolean neverTaken) {
        int roundId = RoundCalculator.roundFromDateTime(ZonedDateTime.now());
        Integer untaken = zoneRepository.countZonesByNotTakenAndUserIdAndRoundIdAndRegionIdAndAreaId(userId, roundId, regionId, areaId);

        if (neverTaken) {
            return new TakesColorDistribution(untaken, List.of());
        }

        List<UniqueZoneView> uniqueZones = takeoverRepository.findUniqueZonesForUserRoundAreaAndRegion(roundId, userId, regionId, areaId);
        return new TakesColorDistribution(untaken, uniqueZones);
    }

}
