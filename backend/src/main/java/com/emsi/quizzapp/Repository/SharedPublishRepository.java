package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.ConfQuiz;
import com.emsi.quizzapp.beans.SharedPublish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SharedPublishRepository extends JpaRepository<SharedPublish, Long> {
    Optional<SharedPublish> findByConfQuiz(ConfQuiz confQuiz);
}
