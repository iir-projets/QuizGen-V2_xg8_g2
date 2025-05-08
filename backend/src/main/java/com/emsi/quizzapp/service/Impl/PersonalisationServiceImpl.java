package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.PersonalisationRepository;
import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.Personalisation;
import com.emsi.quizzapp.service.facade.ConfQuizService;
import com.emsi.quizzapp.service.facade.PersonalisationService;
import com.emsi.quizzapp.ws.converter.PersonalisationConverter;
import com.emsi.quizzapp.ws.dto.PersonalisationDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonalisationServiceImpl implements PersonalisationService {

    private final PersonalisationRepository personalisationRepository;
    private final PersonalisationConverter personalisationConverter;
    private final ConfQuizService confQuizService;

    public PersonalisationServiceImpl(PersonalisationRepository personalisationRepository,
                                      PersonalisationConverter personalisationConverter,
                                      ConfQuizService confQuizService) {
        this.personalisationRepository = personalisationRepository;
        this.personalisationConverter = personalisationConverter;
        this.confQuizService = confQuizService;
    }

    @Override
    @Transactional
    public PersonalisationDTO save(PersonalisationDTO personalisationDTO) {
        Personalisation personalisation = personalisationConverter.toEntity(personalisationDTO);
        ConfQuiz confQuiz = confQuizService.findById(personalisationDTO.getQuizId());
        personalisation.setConfQuiz(confQuiz);
        personalisation = personalisationRepository.save(personalisation);
        return personalisationConverter.toDto(personalisation);
    }

    @Override
    public PersonalisationDTO findById(Long id) {
        Personalisation personalisation = personalisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personalisation not found with id: " + id));
        return personalisationConverter.toDto(personalisation);
    }

    @Override
    @Transactional
    public PersonalisationDTO update(PersonalisationDTO personalisationDTO) {
        Personalisation existing = personalisationRepository.findById(personalisationDTO.getId())
                .orElseThrow(() -> new RuntimeException("Personalisation not found with id: " + personalisationDTO.getId()));

        personalisationConverter.copyProperties(personalisationDTO, existing);
        existing = personalisationRepository.save(existing);
        return personalisationConverter.toDto(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        personalisationRepository.deleteById(id);
    }
}