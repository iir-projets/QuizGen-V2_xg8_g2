package com.emsi.quizzapp.ws.facade.auteur;

import com.emsi.quizzapp.service.facade.ConfQuizService;
import com.emsi.quizzapp.ws.dto.ConfQuizDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/basic-quizzes")
public class ConfQuizController {

    private final ConfQuizService confQuizService;

    public ConfQuizController(ConfQuizService confQuizService) {
        this.confQuizService = confQuizService;
    }

    @PostMapping
    public ResponseEntity<ConfQuizDTO> createQuiz(@RequestBody ConfQuizDTO quizDTO) {
        ConfQuizDTO createdQuiz = confQuizService.createQuiz(quizDTO);
        return ResponseEntity.ok(createdQuiz);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConfQuizDTO> getQuizById(@PathVariable Long id) {
        ConfQuizDTO quiz = confQuizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConfQuizDTO> updateQuiz(@PathVariable Long id, @RequestBody ConfQuizDTO quizDTO) {
        ConfQuizDTO updatedQuiz = confQuizService.updateQuiz(id, quizDTO);
        return ResponseEntity.ok(updatedQuiz);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        confQuizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}