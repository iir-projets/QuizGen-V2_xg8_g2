package com.emsi.quizzapp.beans;

import jakarta.persistence.*;

@Entity
@Table(name = "resultat")
public class Resultat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float scoreObtenu;
    private String feedback;

    @OneToOne
    @JoinColumn(name = "tentative_id")
    private Tentative tentative;

    public Resultat() {
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public float getScoreObtenu() {
        return scoreObtenu;
    }
    public void setScoreObtenu(float scoreObtenu) {
        this.scoreObtenu = scoreObtenu;
    }
    public String getFeedback() {
        return feedback;
    }
    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    public Tentative getTentative() {
        return tentative;
    }
    public void setTentative(Tentative tentative) {
        this.tentative = tentative;
    }

}
