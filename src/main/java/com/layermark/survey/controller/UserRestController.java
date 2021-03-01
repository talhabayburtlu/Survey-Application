package com.layermark.survey.controller;

import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.UserDTO;
import com.layermark.survey.mapper.UserMapper;
import com.layermark.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/")
    public void createUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        userService.save(user);
    }
}
