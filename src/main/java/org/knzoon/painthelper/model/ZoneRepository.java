package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {

    @Query(value = "select z.* from `zone` z left outer join unique_zone uz on z.id = uz.zone_id and uz.region_takes_id = :regionTakesId where z.region_id = :regionId and uz.id is null order by z.area_name, z.name", nativeQuery = true)
    List<Zone> findZonesByNotTakenAndRegionIdAndRegionTakesId(@Param("regionId") Long regionId, @Param("regionTakesId") Long regionTakesId);

    @Query(value = "select z.* from `zone` z left outer join unique_zone uz on z.id = uz.zone_id and uz.region_takes_id = :regionTakesId where z.region_id = :regionId and z.area_id = :areaId and uz.id is null order by z.name", nativeQuery = true)
    List<Zone> findZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(@Param("regionId") Long regionId, @Param("regionTakesId") Long regionTakesId, @Param("areaId") Long areaId);

    @Query(value = "select count(1) from `zone` z left outer join unique_zone uz on z.id = uz.zone_id and uz.region_takes_id = :regionTakesId where z.region_id = :regionId and uz.id is null", nativeQuery = true)
    Integer countZonesByNotTakenAndRegionIdAndRegionTakesId(@Param("regionId") Long regionId, @Param("regionTakesId") Long regionTakesId);

    @Query(value = "select count(1) from `zone` z left outer join unique_zone uz on z.id = uz.zone_id and uz.region_takes_id = :regionTakesId where z.region_id = :regionId and z.area_id = :areaId and uz.id is null", nativeQuery = true)
    Integer countZonesByNotTakenAndRegionIdAndRegionTakesIdAndAreaId(@Param("regionId") Long regionId, @Param("regionTakesId") Long regionTakesId, @Param("areaId") Long areaId);

    /*
        select z.* from `zone` z
        left outer join unique_zone uz on z.id = uz.zone_id and uz.region_takes_id = 79
        where z.region_id = 127
        and z.area_id = 2064
        and uz.id is null
        order by z.area_name, z.name;
     */

    @Query(value = "select distinct z.area_id as areaId, z.area_name as area, 0 as antal from `zone` z where z.region_id = :regionId order by z.area_name", nativeQuery = true)
    List<AreaView> findDistinctAreasByRegionId(Long regionId);

    List<Zone> findByNameIn(Set<String> zonenames);

    List<Zone> findByRegionId(Long regionId);

    List<Zone> findByRegionIdAndAreaId(Long regionId, Long areaId);

}
