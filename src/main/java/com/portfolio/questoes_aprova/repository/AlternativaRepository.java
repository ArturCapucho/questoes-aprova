package com.portfolio.questoes_aprova.repository;

import com.portfolio.questoes_aprova.entity.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlternativaRepository extends JpaRepository<Alternativa, Long> {
    List<Alternativa> findByQuestaoIdAndQuestaoAno(Long questaoId, Integer questaoAno);
}
