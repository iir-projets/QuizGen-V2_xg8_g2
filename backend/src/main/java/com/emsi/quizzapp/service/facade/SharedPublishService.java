package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.SharedPublishDTO;

import java.time.LocalDateTime;

public interface SharedPublishService {
    SharedPublishDTO save(SharedPublishDTO sharedPublishDTO);
    SharedPublishDTO findById(Long id);
    SharedPublishDTO findByQuizId(Long quizId);
    SharedPublishDTO update(SharedPublishDTO sharedPublishDTO);
    void deleteById(Long id);
    void publishQuiz(Long quizId, LocalDateTime expiryDate);
    void unpublishQuiz(Long quizId);
}