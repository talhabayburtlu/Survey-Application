package com.layermark.survey.controller;

import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.Topic;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.dto.ApprovalDTO;
import com.layermark.survey.lib.dto.SubmissionDTO;
import com.layermark.survey.lib.dto.TopicDTO;
import com.layermark.survey.lib.resource.TopicResources;
import com.layermark.survey.lib.resource.TopicResultResource;
import com.layermark.survey.mapper.TopicMapper;
import com.layermark.survey.service.AnswerService;
import com.layermark.survey.service.TopicService;
import com.layermark.survey.service.UserService;
import com.layermark.survey.utils.Constants;
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

    @Operation(
            summary = "Gets all approved topics..",
            description = "Gets all approved topics. By using pagination it is possible to get different results.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("") // Gets all topics.
    public TopicResources getAllApprovedTopics(@RequestParam(defaultValue = "1") int page) {
        ArrayList<Topic> approvedTopics = topicService.findAllTopicsApproved();

        page = Math.max(page, 1); // Making page 1 if it is lower than 1.
        // Computing start and end index of page.
        int startIndex = Math.min((page - 1) * Constants.PAGE_SIZE, approvedTopics.size());
        int endIndex = Math.min(page * Constants.PAGE_SIZE, approvedTopics.size());

        ArrayList<Topic> trimmedApprovedTopics = new ArrayList<>(approvedTopics.subList(startIndex, endIndex));
        return new TopicResources(trimmedApprovedTopics, approvedTopics.size());
    }

    @Operation(
            summary = "Creates / Requests topic",
            description = "Creates a topic if authenticated user is an admin, requests a topic if authenticated user " +
                    "is an user. Id field should be omitted beacause it will be automatically determined. " +
                    "This id field is relevant about updating an answer.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/") // Creates/Requests topic
    public Topic createTopic(@RequestBody TopicDTO topicDTO) {
        Topic topic = topicMapper.toEntity(topicDTO);
        topic.setIsApproved(false);

        if (topic.getAnswers() == null || topic.getAnswers().isEmpty()) // Checking if topic has at least one answer or not.
            throw new RuntimeException("A topic must contain at least one answer.");

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
        Topic oldTopic = topicService.findById(topicId);
        Topic topic = topicMapper.toEntity(topicDTO);

        for (Answer answer : oldTopic.getAnswers()) {
            if (!topic.getAnswers().contains(answer))
                answerService.deleteById(answer.getId());
        }

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
    public TopicResultResource getResults(@RequestParam(defaultValue = "1") int page) {
        return topicService.findResults(page);
    }

    @Operation(
            summary = "Submits an answer to a topic.",
            description = "Currently authenticated user can submit an answer with answer's id. " +
                    "Nothing is needed to be entered for topic information because an answer is directly connected to a topic.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/users")
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
    @GetMapping("/users")
    public TopicResources getAvailableTopics(@RequestParam(defaultValue = "1") int page) { // Gets available topics for a user.
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
    public TopicResources getRequestedTopicsFromUsers(@RequestParam(defaultValue = "1") int page) {
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
