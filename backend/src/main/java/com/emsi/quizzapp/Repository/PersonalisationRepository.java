package com.emsi.quizzapp.Repository;


import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.Personalisation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonalisationRepository extends JpaRepository<Personalisation, Long> {
    Optional<Personalisation> findByConfQuiz(ConfQuiz confQuiz);
}
