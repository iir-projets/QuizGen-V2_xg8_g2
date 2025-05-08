package com.emsi.quizzapp.ws.dto;

import lombok.Data;

@Data
public class EditorQuizDTO {
    private Long id;
    private String questions;
    private Long quizId; // Reference to ConfQuiz
}