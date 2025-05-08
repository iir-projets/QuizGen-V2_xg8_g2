package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.SharedPublish;
import com.emsi.quizzapp.Repository.SharedPublishRepository;
import com.emsi.quizzapp.service.facade.ConfQuizService;
import com.emsi.quizzapp.service.facade.SharedPublishService;
import com.emsi.quizzapp.ws.converter.SharedPublishConverter;
import com.emsi.quizzapp.ws.dto.SharedPublishDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SharedPublishServiceImpl implements SharedPublishService {

    private final SharedPublishRepository sharedPublishRepository;
    private final ConfQuizService confQuizService;
    private final SharedPublishConverter sharedPublishConverter;

    public SharedPublishServiceImpl(SharedPublishRepository sharedPublishRepository,
                                    ConfQuizService confQuizService,
                                    SharedPublishConverter sharedPublishConverter) {
        this.sharedPublishRepository = sharedPublishRepository;
        this.confQuizService = confQuizService;
        this.sharedPublishConverter = sharedPublishConverter;
    }

    @Override
    @Transactional
    public SharedPublishDTO save(SharedPublishDTO sharedPublishDTO) {
        SharedPublish sharedPublish = sharedPublishConverter.toEntity(sharedPublishDTO);
        ConfQuiz confQuiz = confQuizService.findById(sharedPublishDTO.getQuizId());
        sharedPublish.setConfQuiz(confQuiz);
        sharedPublish = sharedPublishRepository.save(sharedPublish);
        return sharedPublishConverter.toDto(sharedPublish);
    }

    @Override
    public SharedPublishDTO findById(Long id) {
        SharedPublish sharedPublish = sharedPublishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SharedPublish not found with id: " + id));
        return sharedPublishConverter.toDto(sharedPublish);
    }

    @Override
    public SharedPublishDTO findByQuizId(Long quizId) {
        ConfQuiz confQuiz = confQuizService.findById(quizId);
        SharedPublish sharedPublish = sharedPublishRepository.findByConfQuiz(confQuiz)
                .orElseThrow(() -> new RuntimeException("SharedPublish not found for quizId: " + quizId));
        return sharedPublishConverter.toDto(sharedPublish);
    }

    @Override
    @Transactional
    public SharedPublishDTO update(SharedPublishDTO sharedPublishDTO) {
        SharedPublish existing = sharedPublishRepository.findById(sharedPublishDTO.getId())
                .orElseThrow(() -> new RuntimeException("SharedPublish not found with id: " + sharedPublishDTO.getId()));

        sharedPublishConverter.copyProperties(sharedPublishDTO, existing);
        existing = sharedPublishRepository.save(existing);
        return sharedPublishConverter.toDto(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        sharedPublishRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void publishQuiz(Long quizId, LocalDateTime expiryDate) {
        ConfQuiz confQuiz = confQuizService.findById(quizId);
        SharedPublish sharedPublish = sharedPublishRepository.findByConfQuiz(confQuiz)
                .orElse(new SharedPublish());

        sharedPublish.setConfQuiz(confQuiz);
        sharedPublish.setPublishDate(LocalDateTime.now());
        sharedPublish.setExpiryDate(expiryDate);
        sharedPublish.setShareLink(generateShareLink(confQuiz));
        sharedPublish.setEmbedCode(generateEmbedCode(confQuiz));

        sharedPublishRepository.save(sharedPublish);
    }

    @Override
    @Transactional
    public void unpublishQuiz(Long quizId) {
        ConfQuiz confQuiz = confQuizService.findById(quizId);
        sharedPublishRepository.findByConfQuiz(confQuiz)
                .ifPresent(sharedPublish -> sharedPublishRepository.delete(sharedPublish));
    }

    private String generateShareLink(ConfQuiz confQuiz) {
        return "https://quizzapp.com/share/" + UUID.randomUUID().toString() + "/quiz/" + confQuiz.getId();
    }

    private String generateEmbedCode(ConfQuiz confQuiz) {
        return "<iframe src=\"https://quizzapp.com/embed/" + confQuiz.getId() + "\" width=\"600\" height=\"400\"></iframe>";
    }
}