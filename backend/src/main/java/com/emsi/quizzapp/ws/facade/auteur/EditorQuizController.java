package com.emsi.quizzapp.ws.facade.auteur;

import com.emsi.quizzapp.service.facade.EditorQuizService;
import com.emsi.quizzapp.ws.dto.EditorQuizDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/editor-quiz")
public class EditorQuizController {

    private final EditorQuizService editorQuizService;

    public EditorQuizController(EditorQuizService editorQuizService) {
        this.editorQuizService = editorQuizService;
    }

    @PostMapping
    public ResponseEntity<EditorQuizDTO> save(@RequestBody EditorQuizDTO editorQuizDTO) {
        EditorQuizDTO saved = editorQuizService.save(editorQuizDTO);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorQuizDTO> findById(@PathVariable Long id) {
        EditorQuizDTO dto = editorQuizService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<EditorQuizDTO> findByQuizId(@PathVariable Long quizId) {
        EditorQuizDTO dto = editorQuizService.findByQuizId(quizId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<EditorQuizDTO> update(@RequestBody EditorQuizDTO editorQuizDTO) {
        EditorQuizDTO updated = editorQuizService.update(editorQuizDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        editorQuizService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}