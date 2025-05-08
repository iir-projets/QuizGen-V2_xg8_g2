package com.emsi.quizzapp.beans;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VF")
public class QuestionVF extends Question {
    private boolean reponseCorrecte;


    public QuestionVF() {
    }

    public boolean isReponseCorrecte() {
        return reponseCorrecte;
    }

    public void setReponseCorrecte(boolean reponseCorrecte) {
        this.reponseCorrecte = reponseCorrecte;
    }
}
