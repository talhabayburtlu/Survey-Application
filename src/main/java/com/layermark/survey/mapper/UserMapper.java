package com.layermark.survey.mapper;

import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.UserDTO;
import com.layermark.survey.lib.resource.UserResource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public User toEntity(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setAnswers(new ArrayList<Answer>());
        return user;
    }

    public UserResource toResource(User user) {
        return modelMapper.map(user, UserResource.class);
    }

}
