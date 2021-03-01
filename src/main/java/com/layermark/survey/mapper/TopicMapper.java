package com.layermark.survey.mapper;

import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.Topic;
import com.layermark.survey.lib.dto.AnswerDTO;
import com.layermark.survey.lib.dto.TopicDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class TopicMapper {
    private final ModelMapper modelMapper = new ModelMapper();
    private final AnswerMapper answerMapper;

    @Autowired
    public TopicMapper(AnswerMapper answerMapper) {
        this.answerMapper = answerMapper;
    }

    public Topic toEntity(TopicDTO topicDTO) {
        Topic topic = modelMapper.map(topicDTO, Topic.class);
        for (AnswerDTO answerDTO : topicDTO.getAnswerDTOS()) {
            Answer answer = answerMapper.toEntity(answerDTO, topic);

            if (topic.getAnswers() == null) // Check if answer list is not initialized
                topic.setAnswers(new ArrayList<>());

            topic.getAnswers().add(answer);
        }

        return topic;
    }


}
