package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.QuizRepository;
import com.emsi.quizzapp.Repository.ResultatRepository;
import com.emsi.quizzapp.Repository.TentativeRepository;
import com.emsi.quizzapp.beans.Quiz;
import com.emsi.quizzapp.beans.Resultat;
import com.emsi.quizzapp.beans.Tentative;
import com.emsi.quizzapp.security.model.User;
import com.emsi.quizzapp.security.repository.UserRepository;
import com.emsi.quizzapp.service.facade.QuizHistoryService;
import com.emsi.quizzapp.ws.dto.QuizHistoryDTO;
import com.emsi.quizzapp.ws.dto.QuizResultDTO;
import com.emsi.quizzapp.ws.dto.QuizStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizHistoryServiceImpl implements QuizHistoryService {

    private final TentativeRepository tentativeRepository;
    private final ResultatRepository resultatRepository;
    private final QuizRepository quizRepository;
    private final UserRepository userRepository;

    @Autowired
    public QuizHistoryServiceImpl(
            TentativeRepository tentativeRepository,
            ResultatRepository resultatRepository,
            QuizRepository quizRepository,
            UserRepository userRepository) {
        this.tentativeRepository = tentativeRepository;
        this.resultatRepository = resultatRepository;
        this.quizRepository = quizRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QuizResultDTO saveQuizResult(QuizResultDTO quizResultDTO) {
        // Get the quiz and participant
        Quiz quiz = quizRepository.findById(quizResultDTO.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + quizResultDTO.getQuizId()));

        User participant = userRepository.findById(quizResultDTO.getParticipantId())
                .orElseThrow(() -> new RuntimeException("Participant not found with id: " + quizResultDTO.getParticipantId()));

        // Create and save the tentative (attempt)
        Tentative tentative = new Tentative();
        tentative.setQuiz(quiz);
        tentative.setParticipant(participant);
        tentative.setDateDebut(LocalDateTime.now().minusMinutes(30)); // Approximate start time
        tentative.setDateFin(quizResultDTO.getSubmissionDate() != null ? quizResultDTO.getSubmissionDate() : LocalDateTime.now());
        tentative.setScore(quizResultDTO.getScore());
        tentative.setEstTerminee(true);

        Tentative savedTentative = tentativeRepository.save(tentative);

        // Create and save the resultat (result)
        Resultat resultat = new Resultat();
        resultat.setTentative(savedTentative);
        resultat.setScoreObtenu(quizResultDTO.getScore());
        resultat.setFeedback(quizResultDTO.isPassed() ? "Félicitations! Vous avez réussi le quiz." : "Vous n'avez pas atteint le score minimum requis.");

        resultatRepository.save(resultat);

        return quizResultDTO;
    }

    @Override
    public List<QuizHistoryDTO> getQuizHistoryByParticipant(Long participantId) {
        List<Tentative> tentatives = tentativeRepository.findByParticipantIdOrderByDateFinDesc(participantId);

        return tentatives.stream()
                .map(this::convertToHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuizStatisticsDTO getStatisticsByParticipant(Long participantId) {
        List<Tentative> tentatives = tentativeRepository.findByParticipantIdOrderByDateFinDesc(participantId);

        QuizStatisticsDTO statistics = new QuizStatisticsDTO();
        statistics.setTotalQuizzes(tentatives.size());

        if (!tentatives.isEmpty()) {
            // Calculate average score
            double averageScore = tentatives.stream()
                    .mapToDouble(Tentative::getScore)
                    .average()
                    .orElse(0.0);
            statistics.setAverageScore(Math.round(averageScore * 10) / 10.0); // Round to 1 decimal place

            // Find top category
            Map<String, Integer> categoryCount = new HashMap<>();
            for (Tentative tentative : tentatives) {
                Quiz quiz = tentative.getQuiz();
                if (quiz != null) {
                    int domaine = quiz.getDomaine();
                    String category = getCategoryFromDomaine(domaine);
                    categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                }
            }

            // Find the category with the highest count
            String topCategory = categoryCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Aucune");

            statistics.setTopCategory(topCategory);
        } else {
            statistics.setAverageScore(0.0);
            statistics.setTopCategory("Aucune");
        }

        return statistics;
    }

    private QuizHistoryDTO convertToHistoryDTO(Tentative tentative) {
        QuizHistoryDTO dto = new QuizHistoryDTO();
        dto.setId(tentative.getId());

        Quiz quiz = tentative.getQuiz();
        if (quiz != null) {
            dto.setQuizId(quiz.getId());
            dto.setTitle(quiz.getTitle());
            dto.setCategory(getCategoryFromDomaine(quiz.getDomaine()));
        }

        dto.setDate(tentative.getDateFin());
        dto.setScore((int) tentative.getScore());

        // Determine if passed based on quiz passing grade
        if (quiz != null && quiz.getPassingGrade() > 0) {
            dto.setPassed(tentative.getScore() >= quiz.getPassingGrade());
        } else {
            dto.setPassed(tentative.getScore() >= 60); // Default passing grade
        }

        return dto;
    }

    private String getCategoryFromDomaine(int domaine) {
        String[] categories = {"CultureG", "Techno", "Science", "Histoire"};
        return categories[domaine % categories.length];
    }
}
