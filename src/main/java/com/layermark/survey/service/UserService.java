package com.layermark.survey.service;

import com.layermark.survey.dao.UserRepository;
import com.layermark.survey.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(int id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null)
            throw new RuntimeException("User with id: " + id + " could not find");

        return user;
    }

    public void save(User user) {
        user.setId(0);
        userRepository.save(user);
    }


}
