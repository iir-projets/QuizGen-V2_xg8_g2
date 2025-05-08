package com.emsi.quizzapp.ws.converter;

import com.emsi.quizzapp.beans.EditorQuiz;
import com.emsi.quizzapp.ws.dto.EditorQuizDTO;
import org.springframework.stereotype.Component;

@Component
public class EditorQuizConverter {

    public EditorQuizDTO toDto(EditorQuiz editorQuiz) {
        if (editorQuiz == null) return null;

        EditorQuizDTO dto = new EditorQuizDTO();
        dto.setId(editorQuiz.getId());
        dto.setQuestions(editorQuiz.getQuestions());
        if (editorQuiz.getConfQuiz() != null) {
            dto.setQuizId(editorQuiz.getConfQuiz().getId());
        }
        return dto;
    }

    public EditorQuiz toEntity(EditorQuizDTO dto) {
        if (dto == null) return null;

        EditorQuiz entity = new EditorQuiz();
        copyProperties(dto, entity);
        return entity;
    }

    public void copyProperties(EditorQuizDTO dto, EditorQuiz entity) {
        if (dto == null || entity == null) return;

        entity.setId(dto.getId());
        entity.setQuestions(dto.getQuestions());
    }
}