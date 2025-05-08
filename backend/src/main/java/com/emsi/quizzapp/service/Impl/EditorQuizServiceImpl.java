package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.EditorQuizRepository;
import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.EditorQuiz;
import com.emsi.quizzapp.service.facade.ConfQuizService;
import com.emsi.quizzapp.service.facade.EditorQuizService;
import com.emsi.quizzapp.ws.converter.EditorQuizConverter;
import com.emsi.quizzapp.ws.dto.EditorQuizDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditorQuizServiceImpl implements EditorQuizService {

    private final EditorQuizRepository editorQuizRepository;
    private final EditorQuizConverter editorQuizConverter;
    private final ConfQuizService confQuizService;

    public EditorQuizServiceImpl(EditorQuizRepository editorQuizRepository,
                                 EditorQuizConverter editorQuizConverter,
                                 ConfQuizService confQuizService) {
        this.editorQuizRepository = editorQuizRepository;
        this.editorQuizConverter = editorQuizConverter;
        this.confQuizService = confQuizService;
    }

    @Override
    @Transactional
    public EditorQuizDTO save(EditorQuizDTO editorQuizDTO) {
        EditorQuiz editorQuiz = editorQuizConverter.toEntity(editorQuizDTO);
        ConfQuiz confQuiz = confQuizService.findById(editorQuizDTO.getQuizId());
        editorQuiz.setConfQuiz(confQuiz);
        editorQuiz = editorQuizRepository.save(editorQuiz);
        return editorQuizConverter.toDto(editorQuiz);
    }

    @Override
    public EditorQuizDTO findById(Long id) {
        EditorQuiz editorQuiz = editorQuizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("EditorQuiz not found with id: " + id));
        return editorQuizConverter.toDto(editorQuiz);
    }

    @Override
    public EditorQuizDTO findByQuizId(Long quizId) {
        ConfQuiz confQuiz = confQuizService.findById(quizId);
        EditorQuiz editorQuiz = editorQuizRepository.findByConfQuiz(confQuiz)
                .orElseThrow(() -> new RuntimeException("EditorQuiz not found for quizId: " + quizId));
        return editorQuizConverter.toDto(editorQuiz);
    }

    @Override
    @Transactional
    public EditorQuizDTO update(EditorQuizDTO editorQuizDTO) {
        EditorQuiz existing = editorQuizRepository.findById(editorQuizDTO.getId())
                .orElseThrow(() -> new RuntimeException("EditorQuiz not found with id: " + editorQuizDTO.getId()));

        editorQuizConverter.copyProperties(editorQuizDTO, existing);
        existing = editorQuizRepository.save(existing);
        return editorQuizConverter.toDto(existing);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        editorQuizRepository.deleteById(id);
    }
}