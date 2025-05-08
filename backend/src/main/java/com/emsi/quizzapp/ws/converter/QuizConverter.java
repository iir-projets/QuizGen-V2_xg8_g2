package com.emsi.quizzapp.ws.converter;

import com.emsi.quizzapp.beans.Quiz;
import com.emsi.quizzapp.security.model.User;
import com.emsi.quizzapp.ws.dto.QuizDTO;
import org.springframework.stereotype.Component;

@Component
public class QuizConverter {

    public QuizDTO toDto(Quiz quiz) {
        if (quiz == null) return null;

        QuizDTO dto = new QuizDTO();
        dto.setId(quiz.getId());
        dto.setTitle(quiz.getTitle());
        dto.setDescription(quiz.getDescription());
        dto.setDomaine(quiz.getDomaine());

        // Configuration
        dto.setAutoCorrection(quiz.getAutoCorrection());
        dto.setIsPublic(quiz.getIsPublic());
        dto.setTimeLimit(quiz.getTimeLimit());

        // Personalization
        dto.setPrimaryColor(quiz.getPrimaryColor());
        dto.setTheme(quiz.getTheme());
        dto.setCompletionMessage(quiz.getCompletionMessage());

        // Advanced parameters
        dto.setRandomizeQuestions(quiz.getRandomizeQuestions());
        dto.setShowResults(quiz.getShowResults());
        dto.setMaxAttempts(quiz.getMaxAttempts());
        dto.setPassingGrade(quiz.getPassingGrade());

        // Publishing
        dto.setPublishDate(quiz.getPublishDate());
        dto.setExpiryDate(quiz.getExpiryDate());
        dto.setShareLink(quiz.getShareLink());
        dto.setEmbedCode(quiz.getEmbedCode());

        // Editor content
        dto.setQuestions(quiz.getQuestions());

        if (quiz.getCreator() != null) {
            dto.setCreatorId(quiz.getCreator().getId());
        }

        return dto;
    }

    public Quiz toEntity(QuizDTO dto) {
        if (dto == null) return null;

        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setDomaine(dto.getDomaine());

        // Configuration
        quiz.setAutoCorrection(dto.getAutoCorrection());
        quiz.setIsPublic(dto.getIsPublic());
        quiz.setTimeLimit(dto.getTimeLimit());

        // Personalization
        quiz.setPrimaryColor(dto.getPrimaryColor());
        quiz.setTheme(dto.getTheme());
        quiz.setCompletionMessage(dto.getCompletionMessage());

        // Advanced parameters
        quiz.setRandomizeQuestions(dto.getRandomizeQuestions());
        quiz.setShowResults(dto.getShowResults());
        quiz.setMaxAttempts(dto.getMaxAttempts());
        quiz.setPassingGrade(dto.getPassingGrade());

        // Publishing
        quiz.setPublishDate(dto.getPublishDate());
        quiz.setExpiryDate(dto.getExpiryDate());
        quiz.setShareLink(dto.getShareLink());
        quiz.setEmbedCode(dto.getEmbedCode());

        // Editor content
        quiz.setQuestions(dto.getQuestions());

        if (dto.getCreatorId() != null) {
            User creator = new User();
            creator.setId(dto.getCreatorId());
            quiz.setCreator(creator);
        }

        return quiz;
    }
}