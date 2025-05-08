package com.emsi.quizzapp.ws.facade.auteur;

import com.emsi.quizzapp.service.facade.SharedPublishService;
import com.emsi.quizzapp.ws.dto.SharedPublishDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/shared-publish")
public class SharedPublishController {

    private final SharedPublishService sharedPublishService;

    public SharedPublishController(SharedPublishService sharedPublishService) {
        this.sharedPublishService = sharedPublishService;
    }

    @PostMapping
    public ResponseEntity<SharedPublishDTO> save(@RequestBody SharedPublishDTO dto) {
        SharedPublishDTO saved = sharedPublishService.save(dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SharedPublishDTO> findById(@PathVariable Long id) {
        SharedPublishDTO dto = sharedPublishService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<SharedPublishDTO> findByQuizId(@PathVariable Long quizId) {
        SharedPublishDTO dto = sharedPublishService.findByQuizId(quizId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping
    public ResponseEntity<SharedPublishDTO> update(@RequestBody SharedPublishDTO dto) {
        SharedPublishDTO updated = sharedPublishService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        sharedPublishService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/publish/{quizId}")
    public ResponseEntity<Void> publishQuiz(@PathVariable Long quizId, @RequestParam LocalDateTime expiryDate) {
        sharedPublishService.publishQuiz(quizId, expiryDate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unpublish/{quizId}")
    public ResponseEntity<Void> unpublishQuiz(@PathVariable Long quizId) {
        sharedPublishService.unpublishQuiz(quizId);
        return ResponseEntity.ok().build();
    }
}