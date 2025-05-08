package com.emsi.quizzapp.ws.converter;

import com.emsi.quizzapp.beans.Personalisation;
import com.emsi.quizzapp.ws.dto.PersonalisationDTO;
import org.springframework.stereotype.Component;

@Component
public class PersonalisationConverter {

    public PersonalisationDTO toDto(Personalisation personalisation) {
        if (personalisation == null) return null;

        PersonalisationDTO dto = new PersonalisationDTO();
        dto.setId(personalisation.getId());
        dto.setPrimaryColor(personalisation.getPrimaryColor());
        dto.setTheme(personalisation.getTheme());
        dto.setCompletionMessage(personalisation.getCompletionMessage());
        if (personalisation.getConfQuiz() != null) {
            dto.setQuizId(personalisation.getConfQuiz().getId());
        }
        return dto;
    }

    public Personalisation toEntity(PersonalisationDTO dto) {
        if (dto == null) return null;

        Personalisation entity = new Personalisation();
        copyProperties(dto, entity);
        return entity;
    }

    public void copyProperties(PersonalisationDTO dto, Personalisation entity) {
        if (dto == null || entity == null) return;

        entity.setId(dto.getId());
        entity.setPrimaryColor(dto.getPrimaryColor());
        entity.setTheme(dto.getTheme());
        entity.setCompletionMessage(dto.getCompletionMessage());
    }
}