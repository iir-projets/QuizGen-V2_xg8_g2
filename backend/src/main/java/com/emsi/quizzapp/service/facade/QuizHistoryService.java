package com.emsi.quizzapp.service.facade;

import com.emsi.quizzapp.ws.dto.QuizHistoryDTO;
import com.emsi.quizzapp.ws.dto.QuizResultDTO;
import com.emsi.quizzapp.ws.dto.QuizStatisticsDTO;

import java.util.List;

public interface QuizHistoryService {
    /**
     * Save a quiz result to history
     * @param quizResultDTO The quiz result to save
     * @return The saved quiz result
     */
    QuizResultDTO saveQuizResult(QuizResultDTO quizResultDTO);
    
    /**
     * Get quiz history for a participant
     * @param participantId The participant ID
     * @return List of quiz history items
     */
    List<QuizHistoryDTO> getQuizHistoryByParticipant(Long participantId);
    
    /**
     * Get global statistics for a participant
     * @param participantId The participant ID
     * @return Global statistics
     */
    QuizStatisticsDTO getStatisticsByParticipant(Long participantId);
}
