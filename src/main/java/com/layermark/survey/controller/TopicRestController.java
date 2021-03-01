package com.layermark.survey.controller;

import com.layermark.survey.entity.Topic;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.ApprovalDTO;
import com.layermark.survey.lib.dto.SubmissionDTO;
import com.layermark.survey.lib.dto.TopicDTO;
import com.layermark.survey.lib.resource.ResultResource;
import com.layermark.survey.mapper.TopicMapper;
import com.layermark.survey.service.AnswerService;
import com.layermark.survey.service.TopicService;
import com.layermark.survey.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        topic.setIsApproved(false);

        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername());

        if (user.getRole().equals("ADMIN"))
            topic.setIsApproved(true);

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

    @PostMapping("/users/")
    public void submitAnswerToTopic(@RequestBody SubmissionDTO submissionDTO) {
        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername());
        answerService.submitAnswer(user, submissionDTO.getAnswerId());
    }

    @GetMapping("/users/")
    public ArrayList<Topic> getAvailableTopics() {
        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername());
        return topicService.findAvailableTopicsForAUser(user);
    }

    @GetMapping("/requests/")
    public ArrayList<Topic> getRequestedTopicsFromUsers() {
        return topicService.findRequestedTopics();
    }

    @PatchMapping("/requests/")
    public void approveRequestedTopic(@RequestBody ApprovalDTO approvalDTO) {
        Topic approvedTopic = topicService.findById(approvalDTO.getApprovedTopicId());

        if (approvedTopic.getIsApproved())
            throw new RuntimeException("This topic is already approved.");

        approvedTopic.setIsApproved(true);
        topicService.save(approvedTopic);
    }

}
