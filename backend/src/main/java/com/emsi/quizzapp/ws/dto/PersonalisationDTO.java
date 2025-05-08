package com.emsi.quizzapp.ws.dto;

public class PersonalisationDTO {
    private Long id;
    private String primaryColor;
    private String theme;
    private String completionMessage;
    private Long quizId; // Reference to ConfQuiz

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public void setCompletionMessage(String completionMessage) {
        this.completionMessage = completionMessage;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getId() {
        return id;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getTheme() {
        return theme;
    }

    public String getCompletionMessage() {
        return completionMessage;
    }

    public Long getQuizId() {
        return quizId;
    }
}
