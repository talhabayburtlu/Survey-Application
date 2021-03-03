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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

    @Operation(
            summary = "Gets a topic by it's id.",
            description = "Gets a topic by it's id.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{topicId}") // Gets topic by it's id.
    public Topic getById(@PathVariable int topicId) {
        return topicService.findById(topicId);
    }

    @PostMapping("/") // Creates/Requests topic
    @Operation(
            summary = "Creates / Requests topic",
            description = "Creates a topic if authenticated user is an admin, requests a topic if authenticated user " +
                    "is an user.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
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

    @Operation(
            summary = "Updates a topic with given request body.",
            description = "Request body needs description and also it is enough to put answers array only one record to" +
                    " create new answer (No need to write existing ones).",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{topicId}") // Updates a topic.
    public Topic updateTopic(@PathVariable int topicId, @RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setIsApproved(true);
        topic.setId(topicId);
        this.topicService.save(topic);
        return topic;
    }

    @Operation(summary = "Deletes a topic by it's id.", description = "Deletes a topic by it's id. Also related answer are deleted as well.")
    @DeleteMapping("/{topicId}") // Deletes a topic.
    public void deleteTopic(@PathVariable int topicId) {
        this.topicService.deleteById(topicId);
    }

    @Operation(
            summary = "Gets results of all (approved) topics.",
            description = "Gets results of all (approved) topics." +
                    " The respond includes information about selection rates of answer for each topic.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/results") // Gets results of approved topics.
    public ArrayList<ResultResource> getResults(@RequestParam(defaultValue = "1") int page) {
        return topicService.findResults(page);
    }

    @Operation(
            summary = "Submits an answer to a topic.",
            description = "Currently authenticated user can submit an answer with answer's id. " +
                    "Nothing is needed to be entered for topic information because an answer is directly connected to a topic.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/users/")
    public void submitAnswerToTopic(@RequestBody SubmissionDTO submissionDTO) { // Submits answer to a topic.
        org.springframework.security.core.userdetails.User securityUser = // Getting authenticated user details.
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername()); // Getting user by using user details.
        answerService.submitAnswer(user, submissionDTO.getAnswerId());
    }

    @Operation(
            summary = "Get's available (unanswered) topics.",
            description = "Gets available (unanswered) topics for currently authenticated user. " +
                    "By using pagination it is possible get other topics than default page's (1) topic.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/users/")
    public ArrayList<Topic> getAvailableTopics(@RequestParam(defaultValue = "1") int page) { // Gets available topics for a user.
        org.springframework.security.core.userdetails.User securityUser = // Getting authenticated user details.
                (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.findByEmail(securityUser.getUsername()); // Getting user by using user details.
        return topicService.findAvailableTopicsForAUser(user, page);
    }

    @Operation(
            summary = "Gets requested topics from users.",
            description = "Gets requested topics from users. By using " +
                    "pagination it is possible get other topics than default page's (1) topic.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/requests/") // Gets topics that are requested and not approved yet.
    public ArrayList<Topic> getRequestedTopicsFromUsers(@RequestParam(defaultValue = "1") int page) {
        return topicService.findRequestedTopics(page);
    }

    @Operation(
            summary = "Approves a requested topic.",
            description = "Approves a requested topic by placing id of topic " +
                    "to request body. An error might occur if it is an already approved topic.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PatchMapping("/requests/") // Approves a requested topic.
    public void approveRequestedTopic(@RequestBody ApprovalDTO approvalDTO) {
        Topic approvedTopic = topicService.findById(approvalDTO.getApprovedTopicId());

        if (approvedTopic.getIsApproved()) // Checking if it is already approved.
            throw new RuntimeException("This topic is already approved.");

        approvedTopic.setIsApproved(true); // Approving a topic.
        topicService.save(approvedTopic);
    }

}
