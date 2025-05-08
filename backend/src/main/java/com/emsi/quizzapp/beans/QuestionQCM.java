package com.emsi.quizzapp.beans;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
@DiscriminatorValue("QCM")
public class QuestionQCM extends Question {
    private boolean estMultiple;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Option> options;


    public QuestionQCM() {
    }

    public boolean isEstMultiple() {
        return estMultiple;
    }

    public void setEstMultiple(boolean estMultiple) {
        this.estMultiple = estMultiple;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }
}
