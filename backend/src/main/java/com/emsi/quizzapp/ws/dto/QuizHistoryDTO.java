package com.emsi.quizzapp.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class QuizHistoryDTO {
    private Long id;
    private Long quizId;
    private String title;
    private String category;
    private LocalDateTime date;
    private int score;
    private boolean passed;
}
