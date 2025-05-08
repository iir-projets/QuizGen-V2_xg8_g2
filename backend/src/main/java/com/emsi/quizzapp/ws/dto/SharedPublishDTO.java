package com.emsi.quizzapp.ws.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SharedPublishDTO {
    private Long id;
    private LocalDateTime publishDate;
    private LocalDateTime expiryDate;
    private String shareLink;
    private String embedCode;
    private Long quizId;
}