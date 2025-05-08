package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.ConfQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfQuizRepository extends JpaRepository<ConfQuiz, Long> {

}
