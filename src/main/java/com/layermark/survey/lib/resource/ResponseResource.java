package com.layermark.survey.lib.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResource {
    @JsonProperty("option-no")
    private int optionNo;
    private String option;
    @JsonProperty("response-count")
    private int responseCount;
}
