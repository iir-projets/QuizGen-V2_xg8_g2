package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.ParamAvanceDTO;

public interface ParamAvanceService {
    ParamAvanceDTO save(ParamAvanceDTO paramAvanceDTO);
    ParamAvanceDTO findById(Long id);
    ParamAvanceDTO findByQuizId(Long quizId);
    ParamAvanceDTO update(ParamAvanceDTO paramAvanceDTO);
    void deleteById(Long id);
}