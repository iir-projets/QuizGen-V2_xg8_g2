package com.emsi.quizzapp.ws.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizDTO {
    private Long id;
    private String title;
    private String description;
    private Integer domaine;

    // Configuration
    private Boolean autoCorrection;
    private Boolean isPublic;
    private String timeLimit;

    // Personalization
    private String primaryColor;
    private String theme;
    private String completionMessage;

    // Advanced parameters
    private Boolean randomizeQuestions;
    private Boolean showResults;
    private Integer maxAttempts;
    private Integer passingGrade;

    // Publishing
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private String shareLink;
    private String embedCode;

    // Editor content
    private String questions;

    private Long creatorId;

}