package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.EditorQuiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EditorQuizRepository extends JpaRepository<EditorQuiz, Long> {
    Optional<EditorQuiz> findByConfQuiz(ConfQuiz confQuiz);

    Optional<EditorQuiz> findByConfQuizId(Long quizId);

}
