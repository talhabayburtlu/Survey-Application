package com.layermark.survey.lib.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResource {
    private int id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private int age;
}
