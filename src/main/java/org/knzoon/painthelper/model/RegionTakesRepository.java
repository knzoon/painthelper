package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionTakesRepository extends JpaRepository<RegionTakes, Long> {
    RegionTakes findByUserIdAndRegionId(Long userId, Long regionId);

    List<RegionTakes> findAllByUserIdOrderByRegionName(Long userId);

    boolean existsByUserIdAndRegionId(Long userId, Long regionId);

    @Query(value = "select distinct rt.user_id as userId from region_takes rt", nativeQuery = true)
    List<UserIdView> findDistinctUsers();

}
