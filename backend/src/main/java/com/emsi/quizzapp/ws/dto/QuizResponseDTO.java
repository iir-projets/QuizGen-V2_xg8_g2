package com.emsi.quizzapp.ws.dto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class QuizResponseDTO {
    private Long quizId;
    private Long participantId;
    private Map<Long, String> answers;
}
