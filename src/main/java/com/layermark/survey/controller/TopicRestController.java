package com.layermark.survey.controller;

import com.layermark.survey.entity.Topic;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.SubmissionDTO;
import com.layermark.survey.lib.dto.TopicDTO;
import com.layermark.survey.lib.resource.ResultResource;
import com.layermark.survey.mapper.TopicMapper;
import com.layermark.survey.service.AnswerService;
import com.layermark.survey.service.TopicService;
import com.layermark.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/topics")
public class TopicRestController {

    private final TopicService topicService;
    private final UserService userService;
    private final AnswerService answerService;
    private final TopicMapper topicMapper;

    @Autowired
    public TopicRestController(TopicService topicService, UserService userService,
                               AnswerService answerService, TopicMapper topicMapper) {
        this.topicService = topicService;
        this.userService = userService;
        this.answerService = answerService;
        this.topicMapper = topicMapper;
    }

    @GetMapping("/results")
    public ArrayList<ResultResource> getResults() {
        return topicService.findResults();
    }

    @PostMapping("/")
    public void createTopic(@RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.toEntity(topicDTO);
        this.topicService.save(topic);
    }

    @PutMapping("/{topicId}")
    public void updateTopic(@PathVariable int topicId, @RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setId(topicId);
        this.topicService.save(topic);
    }

    @DeleteMapping("/{topicId}")
    public void deleteTopic(@PathVariable int topicId) {
        this.topicService.deleteById(topicId);
    }

    @PostMapping("/users/{userId}")
    public void submitAnswer(@RequestBody SubmissionDTO submissionDTO, @PathVariable int userId) {
        User user = userService.findById(userId);
        answerService.submitAnswer(user, submissionDTO.getAnswerId());
    }

    @GetMapping("/users/{userId}")
    public ArrayList<Topic> getAvailableTopics(@PathVariable int userId) {
        User user = userService.findById(userId);
        return topicService.findAvailableTopicsForAUser(user);
    }

}
