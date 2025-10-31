package org.knzoon.painthelper.controller;

import org.knzoon.painthelper.representation.UserRepresentation;
import org.knzoon.painthelper.representation.turfapi.IdParameter;
import org.knzoon.painthelper.representation.turfapi.UserInfoFromTurfApi;
import org.knzoon.painthelper.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/users")
    public List<UserRepresentation> searchUsers(@RequestParam(value = "searchString") String searchString) {
        return userService.searchUsers(searchString);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/api/allusers")
    public List<UserRepresentation> searchAllUsers(@RequestParam(value = "searchString") String searchString) {
        return userService.searchAllUsers(searchString);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/api/userinfo")
    public List<UserInfoFromTurfApi> getUserInfo(@RequestBody List<IdParameter> users) {
        return userService.getUserInfo(users);
    }

}
