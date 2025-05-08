package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.QuizRepository;
import com.emsi.quizzapp.beans.Quiz;

import com.emsi.quizzapp.service.facade.QuizService;
import com.emsi.quizzapp.ws.converter.QuizConverter;
import com.emsi.quizzapp.ws.dto.QuizDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizConverter quizConverter;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository, QuizConverter quizConverter) {
        this.quizRepository = quizRepository;
        this.quizConverter = quizConverter;
    }

    @Override
    public QuizDTO createQuiz(QuizDTO quizDTO) {
        Quiz quiz = quizConverter.toEntity(quizDTO);
        Quiz savedQuiz = quizRepository.save(quiz);
        return quizConverter.toDto(savedQuiz);
    }

    @Override
    public QuizDTO getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(quizConverter::toDto)
                .orElse(null);
    }

    @Override
    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(quizConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuizDTO updateQuiz(Long id, QuizDTO quizDTO) {
        return quizRepository.findById(id)
                .map(existingQuiz -> {
                    Quiz quizUpdate = quizConverter.toEntity(quizDTO);
                    quizUpdate.setId(existingQuiz.getId());
                    Quiz updatedQuiz = quizRepository.save(quizUpdate);
                    return quizConverter.toDto(updatedQuiz);
                })
                .orElse(null);
    }

    @Override
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    @Override
    public List<QuizDTO> getQuizzesByCreator(Long creatorId) {
        return quizRepository.findByCreatorId(creatorId)
                .stream()
                .map(quizConverter::toDto)
                .collect(Collectors.toList());
    }
}