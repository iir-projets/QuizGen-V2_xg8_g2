package com.emsi.quizzapp.ws.converter;

import com.emsi.quizzapp.beans.ParamAvance;
import com.emsi.quizzapp.ws.dto.ParamAvanceDTO;
import org.springframework.stereotype.Component;

@Component
public class ParamAvanceConverter {

    public ParamAvanceDTO toDto(ParamAvance paramAvance) {
        if (paramAvance == null) {
            return null;
        }

        ParamAvanceDTO dto = new ParamAvanceDTO();
        dto.setId(paramAvance.getId());
        dto.setRandomizeQuestions(paramAvance.getRandomizeQuestions());
        dto.setShowResults(paramAvance.getShowResults());
        dto.setMaxAttempts(paramAvance.getMaxAttempts());
        dto.setPassingGrade(paramAvance.getPassingGrade());

        if (paramAvance.getConfQuiz() != null) {
            dto.setQuizId(paramAvance.getConfQuiz().getId());
        }

        return dto;
    }

    public ParamAvance toEntity(ParamAvanceDTO dto) {
        if (dto == null) {
            return null;
        }

        ParamAvance entity = new ParamAvance();
        copyProperties(dto, entity);
        return entity;
    }

    public void copyProperties(ParamAvanceDTO dto, ParamAvance entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setId(dto.getId());
        entity.setRandomizeQuestions(dto.getRandomizeQuestions());
        entity.setShowResults(dto.getShowResults());
        entity.setMaxAttempts(dto.getMaxAttempts());
        entity.setPassingGrade(dto.getPassingGrade());
    }
}