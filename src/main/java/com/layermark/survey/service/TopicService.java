package com.layermark.survey.service;

import com.layermark.survey.dao.TopicRepository;
import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.Topic;
import com.layermark.survey.entity.User;
import com.layermark.survey.lib.resource.ResponseResource;
import com.layermark.survey.lib.resource.ResultResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public ArrayList<ResultResource> findResults() { // Finds results of all topics.
        ArrayList<Topic> topics = findAllTopicsApproved(); // Getting all topics.
        ArrayList<ResultResource> resultResources = new ArrayList<>();

        for (Topic topic : topics) { // Creating result resources by iteration over topics.
            ResultResource resultResource = new ResultResource(topic.getDescription(), new ArrayList<>());
            for (Answer answer : topic.getAnswers()) { // Adding response resources that contains answer related information.
                resultResource.getResponces()
                        .add(new ResponseResource(answer.getId(), answer.getDescription(), answer.getUsers().size()));
            }
            resultResources.add(resultResource);
        }

        return resultResources;
    }

    public ArrayList<Topic> findAvailableTopicsForAUser(User user) { // Finds all unanswered topics for a particular user.
        ArrayList<Topic> allTopics = findAllTopicsApproved();
        ArrayList<Topic> answeredTopics = user.getAnswers() // Getting answered topics.
                .stream()
                .map(Answer::getTopic).collect(Collectors.toCollection(ArrayList::new));

        // Filtering all topics by looking if a topic is listed in answered topics list.
        ArrayList<Topic> topicsAvailable = allTopics
                .stream()
                .filter(topic -> !answeredTopics.contains(topic)).collect(Collectors.toCollection(ArrayList::new));

        return topicsAvailable;
    }

    public ArrayList<Topic> findRequestedTopics() { // Finds requested topics by searching approved property as false.
        return new ArrayList<>(topicRepository.findByIsApprovedFalse());
    }


}
