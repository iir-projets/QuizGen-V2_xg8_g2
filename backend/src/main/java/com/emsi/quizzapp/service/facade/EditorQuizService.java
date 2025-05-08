package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.EditorQuizDTO;

public interface EditorQuizService {
    EditorQuizDTO save(EditorQuizDTO editorQuizDTO);
    EditorQuizDTO findById(Long id);
    EditorQuizDTO findByQuizId(Long quizId);
    EditorQuizDTO update(EditorQuizDTO editorQuizDTO);
    void deleteById(Long id);
}