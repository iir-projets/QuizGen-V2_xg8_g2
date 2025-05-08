package com.emsi.quizzapp.beans;

import jakarta.persistence.*;

@Entity
@Table(name = "options")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String texte;
    private boolean estCorrecte;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionQCM question;

    public Option() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTexte() {
        return texte;
    }
    public void setTexte(String texte) {
        this.texte = texte;
    }
    public boolean isEstCorrecte() {
        return estCorrecte;
    }
    public void setEstCorrecte(boolean estCorrecte) {
        this.estCorrecte = estCorrecte;
    }
    public QuestionQCM getQuestion() {
        return question;
    }
    public void setQuestion(QuestionQCM question) {
        this.question = question;
    }

}
