package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueZoneRepository extends JpaRepository<UniqueZone, Long> {
    UniqueZone findByRegionTakesIdAndZoneZoneId(Long regionTakesId, Long zoneId);

    List<UniqueZone> findByRegionTakesIdAndTakesBetweenOrderByTakesDescZoneAreaAscZoneNameAsc(Long regionTakesId, Integer minTakes, Integer maxTakes);

    List<UniqueZone> findByRegionTakesIdAndZoneAreaIdAndTakesBetweenOrderByTakesDescZoneNameAsc(Long regionTakesId, Long areaId, Integer minTakes, Integer maxTakes);

    Iterable<UniqueZone> findByZoneZoneId(Long zoneId);

    Integer countByRegionTakesIdAndTakesBetween(Long regionTakesId, Integer minTakes, Integer maxTakes);

    Integer countByRegionTakesIdAndZoneAreaIdAndTakesBetween(Long regionTakesId, Long areaId, Integer minTakes, Integer maxTakes);

    @Query(value = "select uz.area_id as areaId, uz.area as area, count(uz.area) as antal from unique_zone uz where uz.region_takes_id = :regionTakesId group by uz.area_id, uz.area order by uz.area", nativeQuery = true)
    List<AreaView> findDistinctAreasByTakenAndRegionTakesId(Long regionTakesId);




}
