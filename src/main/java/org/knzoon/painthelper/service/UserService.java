package org.knzoon.painthelper.service;


import org.knzoon.painthelper.model.User;
import org.knzoon.painthelper.model.UserRepository;
import org.knzoon.painthelper.representation.UserRepresentation;
import org.knzoon.painthelper.representation.turfapi.IdParameter;
import org.knzoon.painthelper.representation.turfapi.UserInfoFromTurfApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserService {
    private final UserRepository userRepository;
    private final TurfApiEndpoint turfApiEndpoint;

    @Autowired
    public UserService(UserRepository userRepository, TurfApiEndpoint turfApiEndpoint) {
        this.userRepository = userRepository;
        this.turfApiEndpoint = turfApiEndpoint;
    }

    @Transactional
    public List<UserInfoFromTurfApi> getUserInfo(List<IdParameter> users) {
        return turfApiEndpoint.getUserInfo(users);
    }

    @Transactional
    public List<UserRepresentation> searchAllUsers(String searchString) {
        if (searchString.isEmpty()) {
            return Collections.emptyList();
        }

        List<User> users = userRepository.findAllByUsernameIsStartingWithOrderByUsername(searchString);
        return users.stream().map(this::toRepresentation).collect(Collectors.toList());
    }

    @Transactional
    public List<UserRepresentation> searchUsers(String searchString) {
        if (searchString.isEmpty()) {
            return Collections.emptyList();
        }
        List<User> users = userRepository.findAllByUsernameIsStartingWithAndImportedIsTrueOrderByUsername(searchString);
        return users.stream().map(this::toRepresentation).collect(Collectors.toList());

    }

    private UserRepresentation toRepresentation(User user) {
        return new UserRepresentation(user.getId(), user.getUsername());
    }

}
