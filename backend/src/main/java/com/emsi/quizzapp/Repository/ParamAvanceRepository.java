package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.ParamAvance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParamAvanceRepository extends JpaRepository<ParamAvance, Long> {
    Optional<ParamAvance> findByConfQuiz(ConfQuiz confQuiz);
}
