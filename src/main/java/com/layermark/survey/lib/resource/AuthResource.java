package com.layermark.survey.lib.resource;

import lombok.Getter;

@Getter
public class AuthResource {
    private final String jwt;
    private final String role;

    public AuthResource(String jwt, String role) {
        this.jwt = jwt;
        this.role = role;
    }

}
