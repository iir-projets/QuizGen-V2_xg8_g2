package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.ws.dto.ConfQuizDTO;

public interface ConfQuizService {
    ConfQuizDTO createQuiz(ConfQuizDTO quizDTO);
    ConfQuizDTO getQuizById(Long id);
    ConfQuizDTO updateQuiz(Long id, ConfQuizDTO quizDTO);
    void deleteQuiz(Long id);
    ConfQuiz findById(Long id); // Méthode utilitaire pour les autres services
}