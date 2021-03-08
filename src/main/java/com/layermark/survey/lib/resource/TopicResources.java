package com.layermark.survey.lib.resource;

import com.layermark.survey.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicResources {
    private ArrayList<Topic> topics;
    private int totalDocumentCount;
}
