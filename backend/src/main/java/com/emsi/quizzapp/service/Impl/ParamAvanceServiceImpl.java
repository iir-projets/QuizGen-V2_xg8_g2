package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.ParamAvanceRepository;
import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.ParamAvance;
import com.emsi.quizzapp.service.facade.ConfQuizService;
import com.emsi.quizzapp.service.facade.ParamAvanceService;
import com.emsi.quizzapp.ws.converter.ParamAvanceConverter;
import com.emsi.quizzapp.ws.dto.ParamAvanceDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParamAvanceServiceImpl implements ParamAvanceService {

    private final ParamAvanceRepository paramAvanceRepository;
    private final ParamAvanceConverter paramAvanceConverter;
    private final ConfQuizService confQuizService;

    public ParamAvanceServiceImpl(ParamAvanceRepository paramAvanceRepository,
                                  ParamAvanceConverter paramAvanceConverter,
                                  ConfQuizService confQuizService) {
        this.paramAvanceRepository = paramAvanceRepository;
        this.paramAvanceConverter = paramAvanceConverter;
        this.confQuizService = confQuizService;
    }

    @Override
    @Transactional
    public ParamAvanceDTO save(ParamAvanceDTO paramAvanceDTO) {
        ParamAvance paramAvance = paramAvanceConverter.toEntity(paramAvanceDTO);
        ConfQuiz confQuiz = confQuizService.findById(paramAvanceDTO.getQuizId());
        paramAvance.setConfQuiz(confQuiz);
        paramAvance = paramAvanceRepository.save(paramAvance);
        return paramAvanceConverter.toDto(paramAvance);
    }

    @Override
    public ParamAvanceDTO findById(Long id) {
        ParamAvance paramAvance = paramAvanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ParamAvance not found with id: " + id));
        return paramAvanceConverter.toDto(paramAvance);
    }

    @Override
    public ParamAvanceDTO findByQuizId(Long quizId) {
        ConfQuiz confQuiz = confQuizService.findById(quizId);
        ParamAvance paramAvance = paramAvanceRepository.findByConfQuiz(confQuiz)
                .orElseThrow(() -> new RuntimeException("ParamAvance not found for quizId: " + quizId));
        return paramAvanceConverter.toDto(paramAvance);
    }

    @Override
    @Transactional
    public ParamAvanceDTO update(ParamAvanceDTO paramAvanceDTO) {
        ParamAvance existing = paramAvanceRepository.findById(paramAvanceDTO.getId())
                .orElseThrow(() -> new RuntimeException("ParamAvance not found with id: " + paramAvanceDTO.getId()));

        paramAvanceConverter.copyProperties(paramAvanceDTO, existing);
        existing = paramAvanceRepository.save(existing);
        return paramAvanceConverter.toDto(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        paramAvanceRepository.deleteById(id);
    }
}