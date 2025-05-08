package com.emsi.quizzapp.ws.converter;

import com.emsi.quizzapp.beans.SharedPublish;
import com.emsi.quizzapp.ws.dto.SharedPublishDTO;
import org.springframework.stereotype.Component;

@Component
public class SharedPublishConverter {

    public SharedPublishDTO toDto(SharedPublish sharedPublish) {
        if (sharedPublish == null) return null;

        SharedPublishDTO dto = new SharedPublishDTO();
        dto.setId(sharedPublish.getId());
        dto.setPublishDate(sharedPublish.getPublishDate());
        dto.setExpiryDate(sharedPublish.getExpiryDate());
        dto.setShareLink(sharedPublish.getShareLink());
        dto.setEmbedCode(sharedPublish.getEmbedCode());

        if (sharedPublish.getConfQuiz() != null) {
            dto.setQuizId(sharedPublish.getConfQuiz().getId());
        }

        return dto;
    }

    public SharedPublish toEntity(SharedPublishDTO dto) {
        if (dto == null) return null;

        SharedPublish entity = new SharedPublish();
        copyProperties(dto, entity);
        return entity;
    }

    public void copyProperties(SharedPublishDTO dto, SharedPublish entity) {
        if (dto == null || entity == null) return;

        entity.setId(dto.getId());
        entity.setPublishDate(dto.getPublishDate());
        entity.setExpiryDate(dto.getExpiryDate());
        entity.setShareLink(dto.getShareLink());
        entity.setEmbedCode(dto.getEmbedCode());
    }
}