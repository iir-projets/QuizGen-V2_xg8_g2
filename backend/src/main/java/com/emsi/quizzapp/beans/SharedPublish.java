package com.emsi.quizzapp.beans;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_publish")
@Data
@NoArgsConstructor
public class SharedPublish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private ConfQuiz confQuiz;

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "share_link", columnDefinition = "TEXT")
    private String shareLink;

    @Column(name = "embed_code", columnDefinition = "TEXT")
    private String embedCode;
}
