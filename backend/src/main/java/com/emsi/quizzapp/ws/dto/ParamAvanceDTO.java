package com.emsi.quizzapp.ws.dto;

import lombok.Data;

@Data
public class ParamAvanceDTO {
    private Long id;
    private Boolean randomizeQuestions;
    private Boolean showResults;
    private Integer maxAttempts;
    private Integer passingGrade;
    private Long quizId;
}