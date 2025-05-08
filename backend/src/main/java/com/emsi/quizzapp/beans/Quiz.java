package com.emsi.quizzapp.beans;

import com.emsi.quizzapp.security.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz")
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer domaine;

    // Configuration options
    @Column(name = "auto_correction", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean autoCorrection = true;

    @Column(name = "is_public", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPublic = false;

    @Column(name = "time_limit", length = 20)
    private String timeLimit;

    // Personalization
    @Column(name = "primary_color", length = 7, columnDefinition = "VARCHAR(7) DEFAULT '#9333EA'")
    private String primaryColor = "#9333EA";

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'light'")
    private String theme = "light";

    @Column(name = "completion_message", columnDefinition = "TEXT")
    private String completionMessage;

    // Advanced parameters
    @Column(name = "randomize_questions", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean randomizeQuestions = false;

    @Column(name = "show_results", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean showResults = true;

    @Column(name = "max_attempts", columnDefinition = "INTEGER DEFAULT 0")
    private Integer maxAttempts = 0;

    @Column(name = "passing_grade", columnDefinition = "INTEGER DEFAULT 60")
    private Integer passingGrade = 60;

    // Publishing
    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Setter
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "share_link", columnDefinition = "TEXT")
    private String shareLink;

    @Setter
    @Column(name = "embed_code", columnDefinition = "TEXT")
    private String embedCode;

    // Editor content
    @Column(columnDefinition = "json", nullable = false)
    private String questions; // Stores questions in JSON format

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;
}