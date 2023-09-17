package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TakeoverRepository extends JpaRepository<Takeover, Long> {

    List<Takeover> findAllByRoundId(Integer roundId);

    @Query(value = "select t.user_id as userId, t.zone_id as zoneId, z.name as zoneName, z.latitude, z.longitude, t.takeover_time as takeoverTime, t.lost_time as lostTime, t.tp, t.pph, t.`type` from takeover t inner join `zone` z on t.zone_id = z.id WHERE t.round_id = :roundId and z.area_id = :areaId", nativeQuery = true)
    List<TakeoverNeoView> findAllByRoundIdAndAreaId(@Param("roundId") Integer roundId, @Param("areaId") Long areaId);

    // Gamla buggiga implementationen
    //    @Query(value = "SELECT * from takeover t WHERE t.round_id = :roundId and t.zone_id = :zoneId and t.user_id = :userId order by t.id desc limit 1", nativeQuery = true)
    // List<Takeover> findPreviousTakeoverForZone(@Param("roundId") Integer roundId, @Param("zoneId") Long zoneId, @Param("userId") Long userId);

    // Första patchen som tyvärr var buggig och sabbade första takeovers för en zon
    //    @Query(value = "SELECT * from takeover t WHERE t.round_id = :roundId and t.zone_id = :zoneId and t.user_id = :userId and t.previous_user_id <> :userId order by t.id desc limit 1", nativeQuery = true)
    //    List<Takeover> findPreviousTakeoverForZone(@Param("roundId") Integer roundId, @Param("zoneId") Long zoneId, @Param("userId") Long userId);

    @Query(value = "SELECT * from takeover t WHERE t.round_id = :roundId and t.zone_id = :zoneId and t.user_id = :userId and ((t.previous_user_id is null) or (t.previous_user_id <> :userId)) order by t.id desc limit 1", nativeQuery = true)
    List<Takeover> findPreviousTakeoverForZone(@Param("roundId") Integer roundId, @Param("zoneId") Long zoneId, @Param("userId") Long userId);


    @Query(value = "SELECT z.name as zoneName, z.area_name as areaName, z.latitude, z.longitude, count(*) as takes FROM takeover t INNER JOIN `zone` z on t.zone_id = z.id WHERE t.round_id = :roundId and t.user_id = :userId and z.region_id = :regionId GROUP BY t.zone_id ORDER BY takes desc, zoneName", nativeQuery = true)
    List<UniqueZoneView> findUniqueZonesForUserRoundAndRegion(@Param("roundId") Integer roundId, @Param("userId") Long userId, @Param("regionId") Long regionId);

    @Query(value = "SELECT z.name as zoneName, z.area_name as areaName, z.latitude, z.longitude, count(*) as takes FROM takeover t INNER JOIN `zone` z on t.zone_id = z.id WHERE t.round_id = :roundId and t.user_id = :userId and z.region_id = :regionId and z.area_id = :areaId GROUP BY t.zone_id ORDER BY takes desc, zoneName", nativeQuery = true)
    List<UniqueZoneView> findUniqueZonesForUserRoundAreaAndRegion(@Param("roundId") Integer roundId, @Param("userId") Long userId, @Param("regionId") Long regionId, @Param("areaId") Long areaId);

    List<Takeover> findAllByRoundIdAndUserOrderById(Integer roundId, User user);



}
