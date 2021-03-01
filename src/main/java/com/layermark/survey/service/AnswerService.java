package com.layermark.survey.service;

import com.layermark.survey.dao.AnswerRepository;
import com.layermark.survey.entity.Answer;
import com.layermark.survey.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public Answer findById(int id) {
        Answer answer = answerRepository.findById(id).orElse(null);
        if (answer == null)
            throw new RuntimeException("Answer with id: " + id + " could not found.");
        return answer;
    }

    public void save(Answer answer) {
        answerRepository.save(answer);
    }

    public void saveAll(List<Answer> answers) {
        answerRepository.saveAll(answers);
    }

    public void deleteById(int id) {
        answerRepository.deleteById(id);
    }

    public void submitAnswer(User user, int answerId) {
        Answer answer = findById(answerId);

        if (!answer.getTopic().getIsApproved()) // Check if submitted answer's topic is approved or not.
            throw new RuntimeException("This topic is not approved yet.");

        if (answer.getUsers().contains(user)) // Check if this answer is already submitted.
            throw new RuntimeException("User is already submitted this answer.");

        for (Answer answer1 : answer.getTopic().getAnswers()) // Check if answered topic is already answered.
            if (answer1.getUsers().contains(user))
                throw new RuntimeException("User is already submitted to this topic.");

        user.getAnswers().add(answer);
        answer.getUsers().add(user);

        this.save(answer);
    }


}
