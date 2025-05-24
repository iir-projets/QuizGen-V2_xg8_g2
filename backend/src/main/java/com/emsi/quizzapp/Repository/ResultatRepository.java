package com.emsi.quizzapp.Repository;

import com.emsi.quizzapp.beans.Resultat;
import com.emsi.quizzapp.beans.Tentative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultatRepository extends JpaRepository<Resultat, Long> {
    Optional<Resultat> findByTentative(Tentative tentative);
    Optional<Resultat> findByTentativeId(Long tentativeId);
}
