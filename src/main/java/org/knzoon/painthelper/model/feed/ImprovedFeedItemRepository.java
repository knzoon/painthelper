package org.knzoon.painthelper.model.feed;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImprovedFeedItemRepository extends JpaRepository<ImprovedFeedItem, UUID> {

    ImprovedFeedItem findFirstByOrderByOrderNumber();
    List<ImprovedFeedItem> findTop200ByOrderNumberGreaterThanOrderByOrderNumber(Long lastReadOrdernumber);
}
