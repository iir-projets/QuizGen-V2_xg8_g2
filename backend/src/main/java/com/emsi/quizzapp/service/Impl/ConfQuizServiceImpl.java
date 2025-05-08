package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.*;
import com.emsi.quizzapp.beans.*;
import org.springframework.beans.BeanUtils;
import com.emsi.quizzapp.service.facade.ConfQuizService;
import com.emsi.quizzapp.ws.converter.ConfQuizConverter;
import com.emsi.quizzapp.ws.dto.ConfQuizDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfQuizServiceImpl implements ConfQuizService {

    private final ConfQuizRepository confQuizRepository;
    private final EditorQuizRepository editorQuizRepository;
    private final PersonalisationRepository personalisationRepository;
    private final ParamAvanceRepository paramAvanceRepository;
    private final SharedPublishRepository sharedPublishRepository;
    private final ConfQuizConverter confQuizConverter;

    public ConfQuizServiceImpl(ConfQuizRepository confQuizRepository,
                               EditorQuizRepository editorQuizRepository,
                               PersonalisationRepository personalisationRepository,
                               ParamAvanceRepository paramAvanceRepository,
                               SharedPublishRepository sharedPublishRepository,
                               ConfQuizConverter confQuizConverter) {
        this.confQuizRepository = confQuizRepository;
        this.editorQuizRepository = editorQuizRepository;
        this.personalisationRepository = personalisationRepository;
        this.paramAvanceRepository = paramAvanceRepository;
        this.sharedPublishRepository = sharedPublishRepository;
        this.confQuizConverter = confQuizConverter;
    }

    @Override
    @Transactional
    public ConfQuizDTO createQuiz(ConfQuizDTO quizDTO) {
        ConfQuiz confQuiz = confQuizConverter.toConfQuizEntity(quizDTO);
        confQuiz = confQuizRepository.save(confQuiz);
        return confQuizConverter.toDto(confQuiz, null, null, null, null);
    }

    @Override
    public ConfQuizDTO getQuizById(Long id) {
        ConfQuiz confQuiz = confQuizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        EditorQuiz editorQuiz = editorQuizRepository.findByConfQuiz(confQuiz).orElse(null);
        Personalisation personalisation = personalisationRepository.findByConfQuiz(confQuiz).orElse(null);
        ParamAvance paramAvance = paramAvanceRepository.findByConfQuiz(confQuiz).orElse(null);
        SharedPublish sharedPublish = sharedPublishRepository.findByConfQuiz(confQuiz).orElse(null);

        return confQuizConverter.toDto(confQuiz, personalisation, paramAvance, sharedPublish, editorQuiz);
    }

    @Override
    @Transactional
    public ConfQuizDTO updateQuiz(Long id, ConfQuizDTO quizDTO) {
        ConfQuiz existing = confQuizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));

        confQuizConverter.copyProperties(quizDTO, existing);
        existing = confQuizRepository.save(existing);

        EditorQuiz editorQuiz = editorQuizRepository.findByConfQuiz(existing).orElse(null);
        Personalisation personalisation = personalisationRepository.findByConfQuiz(existing).orElse(null);
        ParamAvance paramAvance = paramAvanceRepository.findByConfQuiz(existing).orElse(null);
        SharedPublish sharedPublish = sharedPublishRepository.findByConfQuiz(existing).orElse(null);

        return confQuizConverter.toDto(existing, personalisation, paramAvance, sharedPublish, editorQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        confQuizRepository.deleteById(id);
    }

    @Override
    public ConfQuiz findById(Long id) {
        return confQuizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }
}