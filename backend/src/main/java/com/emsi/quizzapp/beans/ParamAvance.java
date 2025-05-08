package com.emsi.quizzapp.beans;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "param_avance")
@Data
@NoArgsConstructor
public class ParamAvance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private ConfQuiz confQuiz;

    @Column(name = "randomize_questions", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean randomizeQuestions = false;

    @Column(name = "show_results", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean showResults = true;

    @Column(name = "max_attempts", columnDefinition = "INTEGER DEFAULT 0")
    private Integer maxAttempts = 0;

    @Column(name = "passing_grade", columnDefinition = "INTEGER DEFAULT 60")
    private Integer passingGrade = 60;
}
