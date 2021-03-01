package com.layermark.survey.controller;

import com.layermark.survey.config.JwtUtil;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.AuthDTO;
import com.layermark.survey.lib.dto.UserDTO;
import com.layermark.survey.lib.resource.AuthResource;
import com.layermark.survey.lib.resource.UserResource;
import com.layermark.survey.mapper.UserMapper;
import com.layermark.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtTokenUtil;
    private final PasswordEncoder bcryptEncoder;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                          JwtUtil jwtTokenUtil,
                          PasswordEncoder bcryptEncoder, UserService userService, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.bcryptEncoder = bcryptEncoder;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public AuthResource login(@RequestBody AuthDTO authDTO) throws Exception {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getEmail(), authDTO.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect email and password.");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authDTO.getEmail());
        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return new AuthResource(jwt);
    }

    @PostMapping("/register")
    public UserResource registerUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);

        // Checking user role, if it is other than USER or ADMIN, revert to USER (default).
        if (user.getRole() == null || !user.getRole().equals("USER"))
            user.setRole("USER");

        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        userService.save(user);
        return userMapper.toResource(user);
    }

}