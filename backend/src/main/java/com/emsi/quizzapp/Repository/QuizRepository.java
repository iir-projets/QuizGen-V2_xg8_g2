package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    // Custom queries can be added here if needed
    List<Quiz> findByCreatorId(Long creatorId);

    List<Quiz> findByIsPublicTrue();
}
