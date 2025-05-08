package com.emsi.quizzapp.ws.converter;

import com.emsi.quizzapp.beans.*;
import com.emsi.quizzapp.ws.dto.ConfQuizDTO;
import org.springframework.stereotype.Component;

@Component
public class ConfQuizConverter {

    public ConfQuizDTO toDto(ConfQuiz confQuiz,
                             Personalisation personalisation,
                             ParamAvance paramAvance,
                             SharedPublish sharedPublish,
                             EditorQuiz editorQuiz) {
        ConfQuizDTO dto = new ConfQuizDTO();

        // ConfQuiz
        dto.setId(confQuiz.getId());
        dto.setTitle(confQuiz.getTitle());
        dto.setDescription(confQuiz.getDescription());
        dto.setDomaine(confQuiz.getDomaine());
        dto.setAutoCorrection(confQuiz.getAutoCorrection());
        dto.setIsPublic(confQuiz.getIsPublic());
        dto.setTimeLimit(confQuiz.getTimeLimit());

        // Personalisation
        if(personalisation != null) {
            dto.setPrimaryColor(personalisation.getPrimaryColor());
            dto.setTheme(personalisation.getTheme());
            dto.setCompletionMessage(personalisation.getCompletionMessage());
        }

        // ParamAvance
        if(paramAvance != null) {
            dto.setRandomizeQuestions(paramAvance.getRandomizeQuestions());
            dto.setShowResults(paramAvance.getShowResults());
            dto.setMaxAttempts(paramAvance.getMaxAttempts());
            dto.setPassingGrade(paramAvance.getPassingGrade());
        }

        // SharedPublish
        if(sharedPublish != null) {
            dto.setPublishDate(sharedPublish.getPublishDate());
            dto.setExpiryDate(sharedPublish.getExpiryDate());
            dto.setShareLink(sharedPublish.getShareLink());
            dto.setEmbedCode(sharedPublish.getEmbedCode());
        }

        // EditorQuiz
        if(editorQuiz != null) {
            dto.setQuestions(editorQuiz.getQuestions());
        }

        return dto;
    }

    public ConfQuiz toConfQuizEntity(ConfQuizDTO dto) {
        ConfQuiz confQuiz = new ConfQuiz();
        confQuiz.setId(dto.getId());
        confQuiz.setTitle(dto.getTitle());
        confQuiz.setDescription(dto.getDescription());
        confQuiz.setDomaine(dto.getDomaine());
        confQuiz.setAutoCorrection(dto.getAutoCorrection());
        confQuiz.setIsPublic(dto.getIsPublic());
        confQuiz.setTimeLimit(dto.getTimeLimit());
        return confQuiz;
    }

    public void copyProperties(ConfQuizDTO quizDTO, ConfQuiz existing) {
        existing.setTitle(quizDTO.getTitle());
        existing.setDescription(quizDTO.getDescription());
        existing.setDomaine(quizDTO.getDomaine());
        existing.setAutoCorrection(quizDTO.getAutoCorrection());
        existing.setIsPublic(quizDTO.getIsPublic());
        existing.setTimeLimit(quizDTO.getTimeLimit());
    }

    // Ajoutez des méthodes similaires pour les autres entités si nécessaire
}
