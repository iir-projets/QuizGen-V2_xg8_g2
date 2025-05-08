package com.emsi.quizzapp.beans;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tentative")
public class Tentative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private float score;
    private boolean estTerminee;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;


    public Tentative() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }
}
