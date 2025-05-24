package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.Tentative;
import com.emsi.quizzapp.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TentativeRepository extends JpaRepository<Tentative, Long> {
    List<Tentative> findByParticipantOrderByDateFinDesc(User participant);
    List<Tentative> findByParticipantIdOrderByDateFinDesc(Long participantId);
    List<Tentative> findByQuizIdAndParticipantId(Long quizId, Long participantId);
}
