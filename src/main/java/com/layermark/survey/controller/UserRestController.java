package com.layermark.survey.controller;

import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.UserDTO;
import com.layermark.survey.lib.resource.UserResource;
import com.layermark.survey.mapper.UserMapper;
import com.layermark.survey.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final PasswordEncoder bcryptEncoder;

    @Autowired
    public UserRestController(UserService userService, UserMapper userMapper, PasswordEncoder bcryptEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.bcryptEncoder = bcryptEncoder;
    }

    @Operation(
            summary = "Creates a user.",
            description = "Creates a user without verifying process because the creator is an admin.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/")
    public UserResource createUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);

        // Setting user role as user if not specified, or not user or not admin.
        if (user.getRole() == null || !user.getRole().equals("USER") || !user.getRole().equals("ADMIN"))
            user.setRole("USER");

        // Skipping verifying because an admin is creating user.

        // Setting password by encoding it.
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        userService.save(user);
        return userMapper.toResource(user);
    }
}
