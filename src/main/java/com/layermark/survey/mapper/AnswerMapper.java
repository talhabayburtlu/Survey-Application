package com.layermark.survey.mapper;

import com.layermark.survey.dao.AnswerRepository;
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
    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerMapper(TopicRepository topicRepository, AnswerRepository answerRepository) {
        this.topicRepository = topicRepository;
        this.answerRepository = answerRepository;
    }

    public Answer toEntity(AnswerDTO answerDTO, Topic topic) {
        Answer answer = answerRepository.findById(answerDTO.getId()).orElse(null);
        if (answer == null) { // If it doesn't exist.
            answer = modelMapper.map(answerDTO, Answer.class);
            answer.setTopic(topic);
            answer.setId(0); // Setting id 0 for creation purpose. Later, hibernate will update it's id.
        } else {
            answer.setDescription(answerDTO.getDescription());
        }
        return answer;
    }

    public AnswerResource toResponse(Answer answer) {
        AnswerResource answerResource = modelMapper.map(answer, AnswerResource.class);
        answerResource.setTopicId(answer.getTopic().getId());
        return answerResource;
    }


}
