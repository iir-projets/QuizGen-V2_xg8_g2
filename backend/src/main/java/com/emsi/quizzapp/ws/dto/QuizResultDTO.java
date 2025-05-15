package com.emsi.quizzapp.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
public class QuizResultDTO {
    private Long quizId;
    private String quizTitle;
    private Long participantId;
    private int score;
    private boolean passed;
    private LocalDateTime submissionDate = LocalDateTime.now();
    private Map<Long, String> correctAnswers; // questionId -> correctAnswer
}
