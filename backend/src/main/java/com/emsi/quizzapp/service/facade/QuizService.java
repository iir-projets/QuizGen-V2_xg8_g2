package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.QuizDTO;
import com.emsi.quizzapp.ws.dto.QuizResponseDTO;
import com.emsi.quizzapp.ws.dto.QuizResultDTO;

import java.util.List;

public interface QuizService {
    QuizDTO createQuiz(QuizDTO quizDTO);
    QuizDTO getQuizById(Long id);
    List<QuizDTO> getAllQuizzes();
    QuizDTO updateQuiz(Long id, QuizDTO quizDTO);
    void deleteQuiz(Long id);
    // QuizService.java
    List<QuizDTO> getQuizzesByCreator(Long creatorId);

    // Les méthodes spécifiques au participant
    List<QuizDTO> findAllPublicQuizzes();
    QuizDTO findQuizById(Long id);
    QuizResultDTO submitQuiz(Long quizId, QuizResponseDTO responseDTO);
    List<QuizDTO> findQuizzesByCreator(Long creatorId);

}
