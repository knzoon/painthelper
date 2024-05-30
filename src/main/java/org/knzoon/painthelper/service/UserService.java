package org.knzoon.painthelper.service;


import org.knzoon.painthelper.representation.turfapi.IdParameter;
import org.knzoon.painthelper.representation.turfapi.UserInfoFromTurfApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserService {
    private final TurfApiEndpoint turfApiEndpoint;

    @Autowired
    public UserService(TurfApiEndpoint turfApiEndpoint) {
        this.turfApiEndpoint = turfApiEndpoint;
    }

    @Transactional
    public List<UserInfoFromTurfApi> getUserInfo(List<IdParameter> users) {
        return turfApiEndpoint.getUserInfo(users);
    }
}
