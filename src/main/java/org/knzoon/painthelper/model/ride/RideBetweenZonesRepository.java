package org.knzoon.painthelper.model.ride;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideBetweenZonesRepository extends JpaRepository<RideBetweenZones, Long> {

    @Query(value = "select t.user_id as userId, z.id as zoneId, t.takeover_time as takeoverTime, z.latitude, z.longitude from takeover t inner join `zone` z on z.id = t.zone_id where round_id = :roundId and user_id = :userId and z.area_id = :areaId order by t.takeover_time", nativeQuery = true)
    List<TakeoverRideView> findTakeoversByRoundUserArea(Integer roundId, Long userId, Long areaId);
}
