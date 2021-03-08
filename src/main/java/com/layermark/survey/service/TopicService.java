package com.layermark.survey.service;

import com.layermark.survey.dao.TopicRepository;
import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.Topic;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.resource.ResponseResource;
import com.layermark.survey.lib.resource.ResultResource;
import com.layermark.survey.lib.resource.TopicResources;
import com.layermark.survey.lib.resource.TopicResultResource;
import com.layermark.survey.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final AnswerService answerService;

    @Autowired
    public TopicService(TopicRepository topicRepository, AnswerService answerService) {
        this.topicRepository = topicRepository;
        this.answerService = answerService;
    }

    public Topic findById(int id) { // Finds topic by its id.
        Topic topic = topicRepository.findById(id).orElse(null);
        if (topic == null)
            throw new RuntimeException("Topic with id: " + id + " could not find.");
        return topic;
    }

    public ArrayList<Topic> findAllTopicsApproved() { // Finds all topics that are approved.
        return new ArrayList<>(topicRepository.findByIsApprovedTrue());
    }

    public void save(Topic topic) {
        topicRepository.save(topic);
        answerService.saveAll(topic.getAnswers()); // Also saving topic's answer'.
    }

    public void deleteById(int id) { // Deletes it by it's id.
        topicRepository.deleteById(id);
    }

    public TopicResultResource findResults(int page) { // Finds results of all topics.
        ArrayList<Topic> topics = findAllTopicsApproved(); // Getting all topics.
        ArrayList<ResultResource> resultResources = new ArrayList<>();

        for (Topic topic : topics) { // Creating result resources by iteration over topics.
            ResultResource resultResource = new ResultResource(topic.getDescription(), new ArrayList<>());
            for (Answer answer : topic.getAnswers()) { // Adding response resources that contains answer related information.
                resultResource.getResponses()
                        .add(new ResponseResource(answer.getId(), answer.getDescription(), answer.getUsers().size()));
            }
            resultResources.add(resultResource);
        }

        page = Math.max(page, 1); // Making page 1 if it is lower than 1.
        // Computing start and end index of page.
        int startIndex = Math.min((page - 1) * Constants.PAGE_SIZE, resultResources.size());
        int endIndex = Math.min(page * Constants.PAGE_SIZE, resultResources.size());

        ArrayList<ResultResource> returnResultResources = new ArrayList<>(resultResources.subList(startIndex, endIndex));
        return new TopicResultResource(returnResultResources, topics.size());
    }

    public TopicResources findAvailableTopicsForAUser(User user, int page) { // Finds all unanswered topics for a particular user.
        ArrayList<Topic> allTopics = findAllTopicsApproved();
        ArrayList<Topic> answeredTopics = user.getAnswers() // Getting answered topics.
                .stream()
                .map(Answer::getTopic).collect(Collectors.toCollection(ArrayList::new));

        // Filtering all topics by looking if a topic is listed in answered topics list.
        ArrayList<Topic> topicsAvailable = allTopics
                .stream()
                .filter(topic -> !answeredTopics.contains(topic)).collect(Collectors.toCollection(ArrayList::new));

        page = Math.max(page, 1); // Making page 1 if it is lower than 1.
        // Computing start and end index of page.
        int startIndex = Math.min((page - 1) * Constants.PAGE_SIZE, topicsAvailable.size());
        int endIndex = Math.min(page * Constants.PAGE_SIZE, topicsAvailable.size());

        ArrayList<Topic> trimmedTopicsAvailable = new ArrayList<>(topicsAvailable.subList(startIndex, endIndex));
        return new TopicResources(trimmedTopicsAvailable, topicsAvailable.size());
    }

    public TopicResources findRequestedTopics(int page) { // Finds requested topics by searching approved property as false.
        List<Topic> requestedTopics = topicRepository.findByIsApprovedFalse();

        page = Math.max(page, 1); // Making page 1 if it is lower than 1.
        // Computing start and end index of page.
        int startIndex = Math.min((page - 1) * Constants.PAGE_SIZE, requestedTopics.size());
        int endIndex = Math.min(page * Constants.PAGE_SIZE, requestedTopics.size());
        ArrayList<Topic> trimmedRequestedTopics = new ArrayList<>(requestedTopics.subList(startIndex, endIndex));
        return new TopicResources(trimmedRequestedTopics, requestedTopics.size());
    }


}
