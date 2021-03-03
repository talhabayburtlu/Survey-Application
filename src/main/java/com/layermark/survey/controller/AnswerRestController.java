package com.layermark.survey.controller;

import com.layermark.survey.entity.Answer;
import com.layermark.survey.lib.dto.AnswerDTO;
import com.layermark.survey.lib.resource.AnswerResource;
import com.layermark.survey.mapper.AnswerMapper;
import com.layermark.survey.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/answers")
public class AnswerRestController {
    private final AnswerService answerService;
    private final AnswerMapper answerMapper;

    @Autowired
    public AnswerRestController(AnswerService answerService, AnswerMapper answerMapper) {
        this.answerService = answerService;
        this.answerMapper = answerMapper;
    }

    @GetMapping("/{answerId}") // Gets answer by it's id.
    public AnswerResource getById(@PathVariable int answerId) {
        Answer answer = answerService.findById(answerId);
        return answerMapper.toResponse(answer);
    }

    @PutMapping("/{answerId}") // Updates answer.
    public AnswerResource updateAnswer(@PathVariable int answerId, @RequestBody AnswerDTO answerDTO) {
        Answer answer = answerService.findById(answerId);
        answer.setDescription(answerDTO.getDescription());

        answerService.save(answer);
        return answerMapper.toResponse(answer);
    }

    @DeleteMapping("/{answerId}") // Deletes answer.
    public void deleteAnswer(@PathVariable int answerId) {
        answerService.deleteById(answerId);
    }

}
