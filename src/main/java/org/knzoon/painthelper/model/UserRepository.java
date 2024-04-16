package org.knzoon.painthelper.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAllByUsernameIsStartingWithAndImportedIsTrueOrderByUsername(String searchstring);

    List<User> findAllByUsernameIsStartingWithOrderByUsername(String searchString);
}
