// QuizDTO.java
package com.emsi.quizzapp.ws.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConfQuizDTO {
    private Long id;
    private String title;
    private String description;
    private Integer domaine;
    private Boolean autoCorrection;
    private Boolean isPublic;
    private String timeLimit;

    // Personalisation
    private String primaryColor;
    private String theme;
    private String completionMessage;

    // ParamAvance
    private Boolean randomizeQuestions;
    private Boolean showResults;
    private Integer maxAttempts;
    private Integer passingGrade;

    // SharedPublish
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private String shareLink;
    private String embedCode;

    // EditorQuiz
    private String questions;
}