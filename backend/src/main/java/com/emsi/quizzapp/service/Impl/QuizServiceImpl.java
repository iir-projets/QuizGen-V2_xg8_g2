package com.emsi.quizzapp.service.Impl;

import com.emsi.quizzapp.Repository.QuizRepository;
import com.emsi.quizzapp.beans.Quiz;
import com.emsi.quizzapp.exception.QuizNotFoundException;
import com.emsi.quizzapp.service.facade.QuizHistoryService;
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
    private final QuizHistoryService quizHistoryService;

    @Autowired
    public QuizServiceImpl(
            QuizRepository quizRepository, 
            QuizConverter quizConverter, 
            ObjectMapper objectMapper,
            QuizHistoryService quizHistoryService) {
        this.quizRepository = quizRepository;
        this.quizConverter = quizConverter;
        this.objectMapper = objectMapper;
        this.quizHistoryService = quizHistoryService;
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
                    new TypeReference<List<Question>>() {
                    }
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
            List<Map<String, Object>> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );

            Map<Long, String> correctAnswers = new HashMap<>();

            for (Map<String, Object> question : questions) {
                // Vérification plus robuste des champs
                Object idObj = question.get("id");
                if (idObj == null) {
                    continue; // ou générer un ID si nécessaire
                }

                Long id = Long.parseLong(idObj.toString());
                String type = (String) question.get("type");

                if ("qcm".equals(type)) {
                    List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
                    if (options != null) {
                        // Find the correct option
                        for (Map<String, Object> option : options) {
                            Object isCorrectObj = option.get("isCorrect");
                            boolean isCorrect = false;

                            // Handle different formats of isCorrect
                            if (isCorrectObj instanceof Boolean) {
                                isCorrect = (Boolean) isCorrectObj;
                            } else if (isCorrectObj instanceof String) {
                                String isCorrectStr = (String) isCorrectObj;
                                isCorrect = "true".equalsIgnoreCase(isCorrectStr) || 
                                            "1".equals(isCorrectStr);
                            } else if (isCorrectObj instanceof Number) {
                                isCorrect = ((Number) isCorrectObj).intValue() == 1;
                            } else if (isCorrectObj != null) {
                                String isCorrectStr = isCorrectObj.toString();
                                isCorrect = "true".equalsIgnoreCase(isCorrectStr) || 
                                            "1".equals(isCorrectStr);
                            }

                            if (isCorrect) {
                                Object textObj = option.get("text");
                                if (textObj != null) {
                                    correctAnswers.put(id, textObj.toString());
                                    break;
                                }
                            }
                        }
                    }
                } else if ("vf".equals(type)) {
                    Object correctAnswerObj = question.get("reponseCorrecte");
                    if (correctAnswerObj != null) {
                        boolean correctAnswer;

                        // Normalize the correct answer to a boolean
                        if (correctAnswerObj instanceof Boolean) {
                            correctAnswer = (Boolean) correctAnswerObj;
                        } else if (correctAnswerObj instanceof String) {
                            String correctAnswerStr = ((String) correctAnswerObj).trim().toLowerCase();
                            correctAnswer = correctAnswerStr.equals("true") || 
                                           correctAnswerStr.equals("vrai") ||
                                           correctAnswerStr.equals("v") ||
                                           correctAnswerStr.equals("1") ||
                                           correctAnswerStr.equals("yes") ||
                                           correctAnswerStr.equals("oui") ||
                                           correctAnswerStr.equals("t");
                        } else if (correctAnswerObj instanceof Number) {
                            correctAnswer = ((Number) correctAnswerObj).intValue() == 1;
                        } else {
                            String correctAnswerStr = correctAnswerObj.toString().trim().toLowerCase();
                            correctAnswer = correctAnswerStr.equals("true") || 
                                           correctAnswerStr.equals("vrai") ||
                                           correctAnswerStr.equals("v") ||
                                           correctAnswerStr.equals("1") ||
                                           correctAnswerStr.equals("yes") ||
                                           correctAnswerStr.equals("oui") ||
                                           correctAnswerStr.equals("t");
                        }

                        // Use 'true' and 'false' strings to match frontend expectations
                        correctAnswers.put(id, correctAnswer ? "true" : "false");
                    }
                }
            }

            return correctAnswers;
        } catch (Exception e) {
            throw new RuntimeException("Error extracting correct answers: " + e.getMessage(), e);
        }
    }

    private String filterCorrectAnswers(String questionsJson) {
        try {
            List<Question> questions = objectMapper.readValue(
                    questionsJson,
                    new TypeReference<List<Question>>() {
                    }
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
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(String correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getExplanation() {
            return explanation;
        }

        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }
    }

    @Override
    public QuizResultDTO submitQuiz(Long quizId, QuizResponseDTO responseDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));

        // Log pour débogage
        System.out.println("Quiz questions JSON: " + quiz.getQuestions());

        try {
            // Essayez de parser les questions d'abord pour vérifier la structure
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> questions = mapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );

            // Si le parsing réussit, continuez
            QuizResultDTO result = new QuizResultDTO();
            result.setQuizId(quizId);
            result.setQuizTitle(quiz.getTitle());
            result.setParticipantId(responseDTO.getParticipantId());

            Map<String, Object> scoreDetails = calculateDetailedScore(quiz, responseDTO);
            result.setScore((int) scoreDetails.get("score"));
            result.setPassed(result.getScore() >= quiz.getPassingGrade());
            result.setSubmissionDate(LocalDateTime.now());

            if (quiz.getAutoCorrection() && quiz.getShowResults()) {
                result.setCorrectAnswers(getCorrectAnswers(quiz));
            }

            // Save quiz result to history
            quizHistoryService.saveQuizResult(result);

            return result;
        } catch (Exception e) {
            // Log l'erreur complète
            System.err.println("Error parsing quiz questions: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Invalid quiz questions format: " + e.getMessage());
        }
    }


    // Méthodes utilitaires pour la gestion des nulls
    private Long safeParseLong(Object obj) {
        if (obj == null) {
            throw new RuntimeException("ID cannot be null");
        }
        try {
            return Long.parseLong(obj.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid ID format: " + obj);
        }
    }

    private String safeGetString(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }


    private Map<Long, String> getQuestionExplanations(Quiz quiz) {
        try {
            List<Map<String, Object>> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Map<String, Object>>>() {
                    }
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


    private Map<String, Object> calculateDetailedScore(Quiz quiz, QuizResponseDTO responseDTO) {
        try {
            // Log pour débogage
            System.out.println("Calculating score for quiz: " + quiz.getId());
            System.out.println("Quiz questions JSON: " + quiz.getQuestions());

            List<Map<String, Object>> questions = objectMapper.readValue(
                    quiz.getQuestions(),
                    new TypeReference<List<Map<String, Object>>>() {
                    }
            );

            Map<Long, String> participantAnswers = responseDTO.getAnswers();
            if (participantAnswers == null) {
                participantAnswers = new HashMap<>();
            }

            System.out.println("Participant answers: " + participantAnswers);

            double totalScore = 0.0;
            double maxPossibleScore = 0.0;
            List<Map<String, Object>> questionResults = new ArrayList<>();

            for (Map<String, Object> question : questions) {
                // Vérification des champs obligatoires
                if (question.get("text") == null || question.get("type") == null) {
                    System.err.println("Question structure is invalid - missing text or type: " + question);
                    continue; // Skip this question instead of failing the entire quiz
                }

                // Génération d'un ID si non présent
                Long questionId;
                try {
                    questionId = question.get("id") != null
                            ? Long.parseLong(question.get("id").toString())
                            : System.currentTimeMillis(); // ID temporaire si non fourni
                } catch (NumberFormatException e) {
                    System.err.println("Invalid question ID format: " + question.get("id"));
                    questionId = System.currentTimeMillis(); // Fallback ID
                }

                String questionType = question.get("type").toString();
                String questionText = question.get("text").toString();

                // Gestion du score avec valeur par défaut
                double questionScore = 1.0; // Valeur par défaut
                if (question.get("score") != null) {
                    try {
                        questionScore = Double.parseDouble(question.get("score").toString());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid score format for question " + questionId + ": " + question.get("score"));
                        // Conserver la valeur par défaut si le score est invalide
                    }
                }

                maxPossibleScore += questionScore;

                Map<String, Object> qResult = new HashMap<>();
                qResult.put("questionId", questionId);
                qResult.put("questionText", questionText);
                qResult.put("questionType", questionType);
                qResult.put("maxScore", questionScore);

                String participantAnswer = participantAnswers.get(questionId);
                boolean isCorrect = false;

                if (participantAnswer != null) {
                    qResult.put("userAnswer", participantAnswer);

                    System.out.println("Checking answer for question " + questionId + 
                                      " of type " + questionType + 
                                      ": user answer = " + participantAnswer);

                    switch (questionType) {
                        case "qcm":
                            isCorrect = checkQCMAnswer(question, participantAnswer);
                            break;
                        case "vf":
                            isCorrect = checkVFAnswer(question, participantAnswer);
                            break;
                        case "text":
                            isCorrect = true; // Toujours correct pour les questions texte
                            break;
                        default:
                            System.err.println("Unknown question type: " + questionType);
                            isCorrect = false;
                    }

                    System.out.println("Answer is " + (isCorrect ? "correct" : "incorrect"));

                    if (isCorrect) {
                        totalScore += questionScore;
                    }
                } else {
                    qResult.put("userAnswer", "Non répondue");
                    System.out.println("No answer provided for question " + questionId);
                }

                qResult.put("isCorrect", isCorrect);
                qResult.put("scoreObtained", isCorrect ? questionScore : 0);
                questionResults.add(qResult);
            }

            // Protection contre la division par zéro
            if (maxPossibleScore <= 0) {
                System.err.println("Warning: maxPossibleScore is " + maxPossibleScore + ", setting to 1 to avoid division by zero");
                maxPossibleScore = 1;
            }

            int finalScore = (int) Math.round((totalScore * 100.0) / maxPossibleScore);
            long totalCorrect = questionResults.stream().filter(q -> (boolean) q.get("isCorrect")).count();

            System.out.println("Final score calculation: " + totalScore + " / " + maxPossibleScore + " * 100 = " + finalScore);
            System.out.println("Total correct answers: " + totalCorrect + " out of " + questions.size());

            Map<String, Object> result = new HashMap<>();
            result.put("score", finalScore);
            result.put("totalCorrect", totalCorrect);
            result.put("totalQuestions", questions.size());
            result.put("questionResults", questionResults);

            return result;
        } catch (Exception e) {
            System.err.println("Error calculating quiz score: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error calculating quiz score: " + e.getMessage(), e);
        }
    }

    // Méthodes pour vérifier les réponses adaptées à votre structure
    private boolean checkQCMAnswer(Map<String, Object> question, String participantAnswer) {
        try {
            if (participantAnswer == null) {
                System.out.println("QCM answer is null");
                return false;
            }

            List<Map<String, Object>> options = (List<Map<String, Object>>) question.get("options");
            if (options == null || options.isEmpty()) {
                System.out.println("QCM options are null or empty for question: " + question.get("text"));
                return false;
            }

            // Log for debugging
            System.out.println("QCM question - participantAnswer: " + participantAnswer);
            System.out.println("QCM question - options count: " + options.size());

            // Normalize participant answer (trim and lowercase)
            String normalizedParticipantAnswer = participantAnswer.trim().toLowerCase();

            // Find the correct option
            for (Map<String, Object> option : options) {
                Object isCorrectObj = option.get("isCorrect");
                boolean isCorrect = false;

                // Handle different formats of isCorrect
                if (isCorrectObj instanceof Boolean) {
                    isCorrect = (Boolean) isCorrectObj;
                } else if (isCorrectObj instanceof String) {
                    String isCorrectStr = (String) isCorrectObj;
                    isCorrect = "true".equalsIgnoreCase(isCorrectStr) || 
                                "1".equals(isCorrectStr);
                } else if (isCorrectObj instanceof Number) {
                    isCorrect = ((Number) isCorrectObj).intValue() == 1;
                } else if (isCorrectObj != null) {
                    String isCorrectStr = isCorrectObj.toString();
                    isCorrect = "true".equalsIgnoreCase(isCorrectStr) || 
                                "1".equals(isCorrectStr);
                }

                if (!isCorrect) {
                    continue; // Skip non-correct options
                }

                Object correctTextObj = option.get("text");
                if (correctTextObj == null) continue;
                String correctText = correctTextObj.toString().trim().toLowerCase();

                System.out.println("QCM option - text: " + correctText + ", isCorrect: " + isCorrect);

                // Simple exact match (case-insensitive)
                if (normalizedParticipantAnswer.equals(correctText)) {
                    System.out.println("QCM answer is correct (exact match): " + participantAnswer);
                    return true;
                }

                // Try additional comparison methods if exact match fails
                if (normalizedParticipantAnswer.equalsIgnoreCase(correctText)) {
                    System.out.println("QCM answer is correct (case-insensitive): " + participantAnswer);
                    return true;
                }

                // Try to match with partial text (if participant answer contains the correct text or vice versa)
                if (normalizedParticipantAnswer.contains(correctText) || correctText.contains(normalizedParticipantAnswer)) {
                    System.out.println("QCM answer is correct (partial match): " + participantAnswer);
                    return true;
                }
            }

            System.out.println("QCM answer is incorrect: " + participantAnswer + " does not match any correct option");
            return false;
        } catch (Exception e) {
            System.err.println("Error checking QCM answer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkVFAnswer(Map<String, Object> question, String participantAnswer) {
        try {
            if (participantAnswer == null) {
                System.out.println("VF answer is null");
                return false;
            }

            Object correctAnswerObj = question.get("reponseCorrecte");
            if (correctAnswerObj == null) {
                System.out.println("Correct answer is null for VF question");
                return false;
            }

            // Log the raw values for debugging
            System.out.println("VF question - correctAnswerObj: " + correctAnswerObj + " (type: " + correctAnswerObj.getClass().getName() + ")");
            System.out.println("VF question - participantAnswer: " + participantAnswer + " (type: " + participantAnswer.getClass().getName() + ")");

            // Normalize the participant answer to a standard format
            String normalizedParticipantAnswer = participantAnswer.trim().toLowerCase();
            boolean participantBoolAnswer;

            // Check for various true values
            if (normalizedParticipantAnswer.equals("true") || 
                normalizedParticipantAnswer.equals("vrai") || 
                normalizedParticipantAnswer.equals("v") ||
                normalizedParticipantAnswer.equals("1") ||
                normalizedParticipantAnswer.equals("yes") ||
                normalizedParticipantAnswer.equals("oui") ||
                normalizedParticipantAnswer.equals("t")) {
                participantBoolAnswer = true;
            } 
            // Check for various false values
            else if (normalizedParticipantAnswer.equals("false") || 
                     normalizedParticipantAnswer.equals("faux") || 
                     normalizedParticipantAnswer.equals("f") ||
                     normalizedParticipantAnswer.equals("0") ||
                     normalizedParticipantAnswer.equals("no") ||
                     normalizedParticipantAnswer.equals("non") ||
                     normalizedParticipantAnswer.equals("n")) {
                participantBoolAnswer = false;
            }
            // Default to the Java boolean parsing if not matched above
            else {
                try {
                    participantBoolAnswer = Boolean.parseBoolean(normalizedParticipantAnswer);
                } catch (Exception e) {
                    System.out.println("Could not parse participant answer as boolean, defaulting to false: " + normalizedParticipantAnswer);
                    participantBoolAnswer = false;
                }
            }

            // Normalize the correct answer to a boolean
            boolean correctAnswer;
            if (correctAnswerObj instanceof Boolean) {
                correctAnswer = (Boolean) correctAnswerObj;
            } else if (correctAnswerObj instanceof String) {
                String correctAnswerStr = ((String) correctAnswerObj).trim().toLowerCase();
                correctAnswer = correctAnswerStr.equals("true") || 
                               correctAnswerStr.equals("vrai") ||
                               correctAnswerStr.equals("v") ||
                               correctAnswerStr.equals("1") ||
                               correctAnswerStr.equals("yes") ||
                               correctAnswerStr.equals("oui") ||
                               correctAnswerStr.equals("t");
            } else if (correctAnswerObj instanceof Number) {
                correctAnswer = ((Number) correctAnswerObj).intValue() == 1;
            } else {
                String correctAnswerStr = correctAnswerObj.toString().trim().toLowerCase();
                correctAnswer = correctAnswerStr.equals("true") || 
                               correctAnswerStr.equals("vrai") ||
                               correctAnswerStr.equals("v") ||
                               correctAnswerStr.equals("1") ||
                               correctAnswerStr.equals("yes") ||
                               correctAnswerStr.equals("oui") ||
                               correctAnswerStr.equals("t");
            }

            System.out.println("VF question - normalized values - correctAnswer: " + correctAnswer + ", participantAnswer: " + participantBoolAnswer);

            return correctAnswer == participantBoolAnswer;
        } catch (Exception e) {
            System.err.println("Error checking VF answer: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthodes utilitaires
    private double safeParseDouble(Object obj, double defaultValue) {
        if (obj == null) return defaultValue;
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
