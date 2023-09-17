package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface BroadcastMessageRepository extends JpaRepository<BroadcastMessage, Long> {
    List<BroadcastMessage> findAllByImportNeededAfterIsAfterOrderByImportNeededAfter(ZonedDateTime lastImport);
}
