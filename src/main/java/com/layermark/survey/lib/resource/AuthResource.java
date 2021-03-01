package com.layermark.survey.lib.resource;

import lombok.Getter;

@Getter
public class AuthResource {
    private final String jwt;

    public AuthResource(String jwt) {
        this.jwt = jwt;
    }

}
