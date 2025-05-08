package com.emsi.quizzapp.beans;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reponse")
public class Reponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateSoumission;
    private String valeur;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "tentative_id")
    private Tentative tentative;

    public Reponse() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getDateSoumission() {
        return dateSoumission;
    }
    public void setDateSoumission(LocalDateTime dateSoumission) {
        this.dateSoumission = dateSoumission;
    }
    public String getValeur() {
        return valeur;
    }
    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }
    public Tentative getTentative() {
        return tentative;
    }
    public void setTentative(Tentative tentative) {
        this.tentative = tentative;
    }

}
