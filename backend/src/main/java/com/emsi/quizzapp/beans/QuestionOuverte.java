package com.emsi.quizzapp.beans;

import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("OUVERTE")
public class QuestionOuverte extends Question {
    @ElementCollection
    @CollectionTable(name = "mots_cles_question", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "mot_cle")
    private List<String> motsCles;


}
