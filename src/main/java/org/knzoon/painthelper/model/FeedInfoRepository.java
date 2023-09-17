package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedInfoRepository extends JpaRepository<FeedInfo, Long> {
    FeedInfo findByFeedName(String feedname);
}
