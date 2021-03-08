package com.layermark.survey.config.security;

import com.layermark.survey.entity.User;
import com.layermark.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findByEmail(email);
        ArrayList<SimpleGrantedAuthority> roles = new ArrayList<>();
        if (user.getRole().equals("ADMIN")) {
            roles.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        } else if (user.getRole().equals("USER"))
            roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        else
            throw new RuntimeException("Account needs to be verified.");

        return new org.springframework.security.core.userdetails.User(email, user.getPassword(), roles);
    }

}