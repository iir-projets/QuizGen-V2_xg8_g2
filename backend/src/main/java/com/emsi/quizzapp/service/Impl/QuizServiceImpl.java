package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.QuizRepository;
import com.emsi.quizzapp.beans.Quiz;

import com.emsi.quizzapp.exception.QuizNotFoundException;
import com.emsi.quizzapp.service.facade.QuizService;
import com.emsi.quizzapp.ws.converter.QuizConverter;
import com.emsi.quizzapp.ws.dto.QuizDTO;
import com.emsi.quizzapp.ws.dto.QuizResponseDTO;
import com.emsi.quizzapp.ws.dto.QuizResultDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizConverter quizConverter;
    private final ObjectMapper objectMapper;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository, QuizConverter quizConverter, ObjectMapper objectMapper) {
        this.quizRepository = quizRepository;
        this.quizConverter = quizConverter;
        this.objectMapper = objectMapper;
    }

    @Override
    public QuizDTO createQuiz(QuizDTO quizDTO) {
        Quiz quiz = quizConverter.toEntity(quizDTO);
        Quiz savedQuiz = quizRepository.save(quiz);
        return quizConverter.toDto(savedQuiz);
    }

    @Override
    public QuizDTO getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(quizConverter::toDto)
                .orElse(null);
    }

    @Override
    public List<QuizDTO> getAllQuizzes() {
        return quizRepository.findAll()
                .stream()
                .map(quizConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuizDTO updateQuiz(Long id, QuizDTO quizDTO) {
        return quizRepository.findById(id)
                .map(existingQuiz -> {
                    Quiz quizUpdate = quizConverter.toEntity(quizDTO);
                    quizUpdate.setId(existingQuiz.getId());
                    Quiz updatedQuiz = quizRepository.save(quizUpdate);
                    return quizConverter.toDto(updatedQuiz);
                })
                .orElse(null);
    }

    @Override
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    @Override
    public List<QuizDTO> getQuizzesByCreator(Long creatorId) {
        return quizRepository.findByCreatorId(creatorId)
                .stream()
                .map(quizConverter::toDto)
                .collect(Collectors.toList());
    }












    @Override
    public List<QuizDTO> findAllPublicQuizzes() {
        return quizRepository.findByIsPublicTrue()
                .stream()
                .map(quizConverter::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public QuizDTO findQuizById(Long id) {
        return quizRepository.findById(id)
                .map(quiz -> {
                    QuizDTO dto = quizConverter.toDto(quiz);
                    if (!quiz.getShowResults()) {
                        dto.setQuestions(filterCorrectAnswers(dto.getQuestions()));
                    }
                    return dto;
                })
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + id));
    }

   // @Override
/*    public QuizResultDTO submitQuiz(Long quizId, QuizResponseDTO responseDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));

        QuizResultDTO result = new QuizResultDTO();
        result.setQuizId(quizId);
        result.setQuizTitle(quiz.getTitle());
        result.setParticipantId(responseDTO.getParticipantId());
        result.setScore(calculateScore(quiz, responseDTO));
        result.setPassed(result.getScore() >= quiz.getPassingGrade());
        result.setSubmissionDate(LocalDateTime.now());

        if (quiz.getAutoCorrection() && quiz.getShowResults()) {
            result.setCorrectAnswers(getCorrectAnswers(quiz));
        }

        return result;
    }*/

    @Override
    public List<QuizDTO> findQuizzesByCreator(Long creatorId) {
        return quizRepository.findByCreatorId(creatorId)
                .stream()
                .map(quizConverter::toDto)
                .collect(Collectors.toList());
    }

    private int calculateScore(Quiz quiz, QuizResponseDTO responseDTO) {
        try {
            List<Question> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Question>>() {}
            );

            Map<Long, String> participantAnswers = responseDTO.getAnswers();

            int correctCount = 0;
            for (Question question : questions) {
                String participantAnswer = participantAnswers.get(question.getId());
                if (participantAnswer != null && participantAnswer.equals(question.getCorrectAnswer())) {
                    correctCount++;
                }
            }

            return (int) Math.round((correctCount * 100.0) / questions.size());
        } catch (Exception e) {
            throw new RuntimeException("Error calculating quiz score", e);
        }
    }

    private Map<Long, String> getCorrectAnswers(Quiz quiz) {
        try {
            List<Question> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Question>>() {}
            );

            return questions.stream()
                    .collect(Collectors.toMap(
                            Question::getId,
                            Question::getCorrectAnswer
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error extracting correct answers", e);
        }
    }

    private String filterCorrectAnswers(String questionsJson) {
        try {
            List<Question> questions = objectMapper.readValue(
                    questionsJson,
                    new TypeReference<List<Question>>() {}
            );

            questions.forEach(q -> q.setCorrectAnswer(null));
            return objectMapper.writeValueAsString(questions);
        } catch (Exception e) {
            throw new RuntimeException("Error filtering correct answers", e);
        }
    }

    private static class Question {
        private Long id;
        private String text;
        private List<String> options;
        private String correctAnswer;
        private String explanation;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public List<String> getOptions() { return options; }
        public void setOptions(List<String> options) { this.options = options; }
        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }




    @Override
    public QuizResultDTO submitQuiz(Long quizId, QuizResponseDTO responseDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));

        QuizResultDTO result = new QuizResultDTO();
        result.setQuizId(quizId);
        result.setQuizTitle(quiz.getTitle());
        result.setParticipantId(responseDTO.getParticipantId());

        // Calcul du score en fonction du type de question
        Map<String, Object> scoreDetails = calculateDetailedScore(quiz, responseDTO);
        result.setScore((int) scoreDetails.get("score"));
        result.setPassed(result.getScore() >= quiz.getPassingGrade());
        result.setSubmissionDate(LocalDateTime.now());
        result.setScoreDetails(scoreDetails);

        if (quiz.getAutoCorrection() && quiz.getShowResults()) {
            result.setCorrectAnswers(getCorrectAnswers(quiz));
            result.setQuestionExplanations(getQuestionExplanations(quiz));
        }

        return result;
    }

    private Map<String, Object> calculateDetailedScore(Quiz quiz, QuizResponseDTO responseDTO) {
        try {
            List<Map<String, Object>> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            Map<Long, String> participantAnswers = responseDTO.getAnswers();
            int totalScore = 0;
            int maxPossibleScore = 0;
            List<Map<String, Object>> questionResults = new ArrayList<>();

            for (Map<String, Object> question : questions) {
                Long questionId = Long.parseLong(question.get("id").toString());
                String questionType = (String) question.get("type");
                double questionScore = Double.parseDouble(question.get("score").toString());
                maxPossibleScore += questionScore;

                Map<String, Object> qResult = new HashMap<>();
                qResult.put("questionId", questionId);
                qResult.put("questionText", question.get("text"));
                qResult.put("questionType", questionType);
                qResult.put("maxScore", questionScore);

                String participantAnswer = participantAnswers.get(questionId);
                boolean isCorrect = false;

                if (participantAnswer != null) {
                    qResult.put("userAnswer", participantAnswer);

                    switch (questionType) {
                        case "qcm":
                            isCorrect = checkQCMAnswer(question, participantAnswer);
                            break;
                        case "vf":
                            isCorrect = checkVFAnswer(question, participantAnswer);
                            break;
                        case "text":
                            // Pour les questions texte, on considère toujours correct (à moins d'avoir une correction manuelle)
                            isCorrect = true;
                            break;
                    }

                    if (isCorrect) {
                        totalScore += questionScore;
                    }
                } else {
                    qResult.put("userAnswer", "Non répondue");
                }

                qResult.put("isCorrect", isCorrect);
                qResult.put("scoreObtained", isCorrect ? questionScore : 0);
                questionResults.add(qResult);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("score", (int) Math.round((totalScore * 100.0) / maxPossibleScore));
            result.put("totalCorrect", questionResults.stream().filter(q -> (boolean) q.get("isCorrect")).count());
            result.put("totalQuestions", questions.size());
            result.put("questionResults", questionResults);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error calculating quiz score", e);
        }
    }

    private boolean checkQCMAnswer(Map<String, Object> question, String participantAnswer) {
        List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
        return options.stream()
                .filter(opt -> "true".equals(opt.get("isCorrect").toString()))
                .anyMatch(opt -> opt.get("text").toString().equals(participantAnswer));
    }

    private boolean checkVFAnswer(Map<String, Object> question, String participantAnswer) {
        boolean correctAnswer = "true".equals(question.get("reponseCorrecte").toString());
        return participantAnswer.equalsIgnoreCase(String.valueOf(correctAnswer));
    }

    private Map<Long, String> getQuestionExplanations(Quiz quiz) {
        try {
            List<Map<String, Object>> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            return questions.stream()
                    .collect(Collectors.toMap(
                            q -> Long.parseLong(q.get("id").toString()),
                            q -> (String) q.get("explication")
                    ));
        } catch (Exception e) {
            throw new RuntimeException("Error extracting question explanations", e);
        }
    }

}
