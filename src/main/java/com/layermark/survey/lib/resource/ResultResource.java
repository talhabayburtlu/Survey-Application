package com.layermark.survey.lib.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultResource {
    private String question;
    private ArrayList<ResponseResource> responses;
}
