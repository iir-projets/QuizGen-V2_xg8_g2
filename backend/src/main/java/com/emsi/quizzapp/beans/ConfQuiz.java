package com.emsi.quizzapp.beans;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "conf_quiz")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfQuiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer domaine;

    @Column(name = "auto_correction", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean autoCorrection = true;

    @Column(name = "is_public", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPublic = false;

    @Column(name = "time_limit", length = 20)
    private String timeLimit;

    @OneToOne(mappedBy = "confQuiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private EditorQuiz editorQuiz;

    @OneToOne(mappedBy = "confQuiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Personalisation personalisation;

    @OneToOne(mappedBy = "confQuiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParamAvance paramAvance;

    @OneToOne(mappedBy = "confQuiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private SharedPublish sharedPublish;
}
