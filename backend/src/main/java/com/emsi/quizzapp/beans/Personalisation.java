package com.emsi.quizzapp.beans;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "personalisation")
@Data
@NoArgsConstructor
public class Personalisation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private ConfQuiz confQuiz;

    @Column(name = "primary_color", length = 7, columnDefinition = "VARCHAR(7) DEFAULT '#9333EA'")
    private String primaryColor = "#9333EA";

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'light'")
    private String theme = "light";

    @Column(name = "completion_message", columnDefinition = "TEXT")
    private String completionMessage;
}
