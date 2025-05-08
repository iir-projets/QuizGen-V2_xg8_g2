package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.PersonalisationDTO;

public interface PersonalisationService {
    PersonalisationDTO save(PersonalisationDTO personalisationDTO);
    PersonalisationDTO findById(Long id);
    PersonalisationDTO update(PersonalisationDTO personalisationDTO);
    void deleteById(Long id);
}