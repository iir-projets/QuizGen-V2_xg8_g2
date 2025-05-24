package com.emsi.quizzapp.ws.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuizStatisticsDTO {
    private int totalQuizzes;
    private double averageScore;
    private String topCategory;
}
