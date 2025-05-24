package com.emsi.quizzapp.ws.facade.participant;

import com.emsi.quizzapp.service.facade.QuizHistoryService;
import com.emsi.quizzapp.ws.dto.QuizHistoryDTO;
import com.emsi.quizzapp.ws.dto.QuizResultDTO;
import com.emsi.quizzapp.ws.dto.QuizStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participant/history")
@CrossOrigin(origins = "*")
public class QuizHistoryController {

    private final QuizHistoryService quizHistoryService;

    @Autowired
    public QuizHistoryController(QuizHistoryService quizHistoryService) {
        this.quizHistoryService = quizHistoryService;
    }

    @PostMapping("/save")
    public ResponseEntity<QuizResultDTO> saveQuizResult(@RequestBody QuizResultDTO quizResultDTO) {
        QuizResultDTO savedResult = quizHistoryService.saveQuizResult(quizResultDTO);
        return ResponseEntity.ok(savedResult);
    }

    @GetMapping("/{participantId}")
    public ResponseEntity<List<QuizHistoryDTO>> getQuizHistory(@PathVariable Long participantId) {
        List<QuizHistoryDTO> history = quizHistoryService.getQuizHistoryByParticipant(participantId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/statistics/{participantId}")
    public ResponseEntity<QuizStatisticsDTO> getStatistics(@PathVariable Long participantId) {
        QuizStatisticsDTO statistics = quizHistoryService.getStatisticsByParticipant(participantId);
        return ResponseEntity.ok(statistics);
    }
}
