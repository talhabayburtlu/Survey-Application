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

    @GetMapping("/{topicId}") // Gets topic by it's id.
    public Topic getById(@PathVariable int topicId) {
        return topicService.findById(topicId);
    }

    @PostMapping("/") // Creates/Requests topic
    public Topic createTopic(@RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setIsApproved(false);

        org.springframework.security.core.userdetails.User securityUser =
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername());

        if (user.getRole().equals("ADMIN")) // Checking if creator of this topic is Admin or not.
            topic.setIsApproved(true); // Not approving if it is created by a user.

        this.topicService.save(topic);
        return topic;
    }

    @PutMapping("/{topicId}") // Updates a topic.
    public Topic updateTopic(@PathVariable int topicId, @RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setIsApproved(true);
        topic.setId(topicId);
        this.topicService.save(topic);
        return topic;
    }

    @DeleteMapping("/{topicId}") // Deletes a topic.
    public void deleteTopic(@PathVariable int topicId) {
        this.topicService.deleteById(topicId);
    }

    @GetMapping("/results") // Gets results of approved topics.
    public ArrayList<ResultResource> getResults(@RequestParam(defaultValue = "1") int page) {
        return topicService.findResults(page);
    }

    @PostMapping("/users/")
    public void submitAnswerToTopic(@RequestBody SubmissionDTO submissionDTO) { // Submits answer to a topic.
        org.springframework.security.core.userdetails.User securityUser = // Getting authenticated user details.
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername()); // Getting user by using user details.
        answerService.submitAnswer(user, submissionDTO.getAnswerId());
    }

    @GetMapping("/users/")
    public ArrayList<Topic> getAvailableTopics(@RequestParam(defaultValue = "1") int page) { // Gets available topics for a user.
        org.springframework.security.core.userdetails.User securityUser = // Getting authenticated user details.
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername()); // Getting user by using user details.
        return topicService.findAvailableTopicsForAUser(user, page);
    }

    @GetMapping("/requests/") // Gets topics that are requested and not approved yet.
    public ArrayList<Topic> getRequestedTopicsFromUsers(@RequestParam(defaultValue = "1") int page) {
        return topicService.findRequestedTopics(page);
    }

    @PatchMapping("/requests/") // Approves a requested topic.
    public void approveRequestedTopic(@RequestBody ApprovalDTO approvalDTO) {
        Topic approvedTopic = topicService.findById(approvalDTO.getApprovedTopicId());

        if (approvedTopic.getIsApproved()) // Checking if it is already approved.
            throw new RuntimeException("This topic is already approved.");

        approvedTopic.setIsApproved(true); // Approving a topic.
        topicService.save(approvedTopic);
    }

}
