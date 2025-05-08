package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.QuizDTO;

import java.util.List;

public interface QuizService {
    QuizDTO createQuiz(QuizDTO quizDTO);
    QuizDTO getQuizById(Long id);
    List<QuizDTO> getAllQuizzes();
    QuizDTO updateQuiz(Long id, QuizDTO quizDTO);
    void deleteQuiz(Long id);
    // QuizService.java
    List<QuizDTO> getQuizzesByCreator(Long creatorId);

}