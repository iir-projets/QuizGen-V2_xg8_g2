package com.emsi.quizzapp.ws.facade.auteur;

import com.emsi.quizzapp.security.model.User;
import com.emsi.quizzapp.service.facade.QuizService;
import com.emsi.quizzapp.ws.dto.QuizDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private static final Logger logger = LoggerFactory.getLogger(QuizController.class);
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO quizDTO) {
        QuizDTO createdQuiz = quizService.createQuiz(quizDTO);
        return ResponseEntity.ok(createdQuiz);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) {
        QuizDTO quizDTO = quizService.getQuizById(id);
        return quizDTO != null ? ResponseEntity.ok(quizDTO) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        List<QuizDTO> quizzes = quizService.getAllQuizzes();
        return ResponseEntity.ok(quizzes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(@PathVariable Long id, @RequestBody QuizDTO quizDTO) {
        QuizDTO updatedQuiz = quizService.updateQuiz(id, quizDTO);
        return updatedQuiz != null ? ResponseEntity.ok(updatedQuiz) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCreator(@PathVariable Long creatorId) {
        List<QuizDTO> quizzes = quizService.getQuizzesByCreator(creatorId);
        return ResponseEntity.ok(quizzes);
    }

    @GetMapping("/my-quizzes")
    public ResponseEntity<List<QuizDTO>> getMyQuizzes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                // Get the authenticated user
                User user = (User) authentication.getPrincipal();
                Long creatorId = user.getId();

                logger.info("Fetching quizzes for authenticated user ID: {}", creatorId);

                List<QuizDTO> quizzes = quizService.getQuizzesByCreator(creatorId);
                return ResponseEntity.ok(quizzes);
            } catch (ClassCastException e) {
                logger.error("Error getting user ID: Authentication principal is not a User instance", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            } catch (Exception e) {
                logger.error("Unexpected error in /my-quizzes endpoint", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        logger.warn("Unauthorized access attempt to /my-quizzes");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}