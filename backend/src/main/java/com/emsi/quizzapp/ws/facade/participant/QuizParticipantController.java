package com.emsi.quizzapp.ws.facade.participant;

import com.emsi.quizzapp.service.facade.QuizService;
import com.emsi.quizzapp.ws.dto.QuizDTO;
import com.emsi.quizzapp.ws.dto.QuizResponseDTO;
import com.emsi.quizzapp.ws.dto.QuizResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant/quizzes")
public class QuizParticipantController {

    private final QuizService quizService;

    @Autowired
    public QuizParticipantController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/public")
    public ResponseEntity<List<QuizDTO>> getAllPublicQuizzes() {
        List<QuizDTO> quizzes = quizService.findAllPublicQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        QuizDTO quiz = quizService.findQuizById(id);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResultDTO> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizResponseDTO responseDTO) {
        QuizResultDTO result = quizService.submitQuiz(quizId, responseDTO);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCreator(@PathVariable Long creatorId) {
        List<QuizDTO> quizzes = quizService.findQuizzesByCreator(creatorId);
        return ResponseEntity.ok(quizzes);
    }
}
