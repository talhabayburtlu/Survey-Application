package com.layermark.survey.mapper;

import com.layermark.survey.dao.TopicRepository;
import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.Topic;
import com.layermark.survey.lib.dto.AnswerDTO;
import com.layermark.survey.lib.resource.AnswerResource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {

    private final ModelMapper modelMapper = new ModelMapper();
    private final TopicRepository topicRepository;

    @Autowired
    public AnswerMapper(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Answer toEntity(AnswerDTO answerDTO, Topic topic) {
        Answer answer = modelMapper.map(answerDTO, Answer.class);
        answer.setTopic(topic);
        return answer;
    }

    public AnswerResource toResponse(Answer answer) {
        AnswerResource answerResource = modelMapper.map(answer, AnswerResource.class);
        answerResource.setTopicId(answer.getTopic().getId());
        return answerResource;
    }


}
