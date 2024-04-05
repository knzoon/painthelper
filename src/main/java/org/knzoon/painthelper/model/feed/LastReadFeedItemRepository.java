package org.knzoon.painthelper.model.feed;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LastReadFeedItemRepository extends JpaRepository<LastReadFeedItem, Long> {
}
