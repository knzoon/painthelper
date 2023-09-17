package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoneUpdatedRepository extends JpaRepository<ZoneUpdated, Long> {
}
