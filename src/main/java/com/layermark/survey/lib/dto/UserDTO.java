package com.layermark.survey.lib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @JsonProperty("full-name")
    private String fullName;
    private String email;
    private String password;
    private String role;
    @JsonProperty("phone-number")
    private String phoneNumber;
    private int age;
}
